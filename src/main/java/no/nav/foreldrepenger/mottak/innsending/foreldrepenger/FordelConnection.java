package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.util.CounterRegistry.FEILET_KVITTERINGER;
import static no.nav.foreldrepenger.common.util.CounterRegistry.FORDELT_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.FP_SENDFEIL;
import static no.nav.foreldrepenger.common.util.CounterRegistry.GITTOPP_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.MANUELL_KVITTERING;
import static no.nav.foreldrepenger.mottak.http.RetryAwareWebClientConfiguration.retryOnlyOn5xxFailures;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.FPFORDEL;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_MIXED;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FordelKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import reactor.core.publisher.Mono;

@Component
public class FordelConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(FordelConnection.class);

    private final FordelConfig cfg;

    protected FordelConnection(@Qualifier(FPFORDEL) WebClient webClient, FordelConfig cfg) {
        super(webClient, cfg);
        this.cfg = cfg;
    }

    public FordelResultat send(Konvolutt konvolutt) {
        try {
            LOG.info("Sender {} til {}", name(konvolutt.getType()), name());
            var kvittering = sendSøknad(konvolutt);
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(konvolutt.getType()), name(), kvittering);
            return kvittering;
        } catch (Exception e) {
            LOG.info("Feil ved sending av {}", konvolutt.getMetadata());
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    private FordelResultat sendSøknad(Konvolutt konvolutt) {
        var leveranseRespons = webClient.post()
            .uri(cfg.fordelEndpoint())
            .contentType(MULTIPART_MIXED)
            .bodyValue(konvolutt.getPayload().getBody())
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity(FordelKvittering.class)
            .doOnRequest(va -> konvolutt.getType().count()) // Skal kjøres hver gang, uavhengig om OK elelr feilet respons!
            .retryWhen(retryOnlyOn5xxFailures(cfg.fordelEndpoint().toString()))
            .onErrorResume(e -> Mono.error(new InnsendingFeiletFpFordelException(e)))
            .defaultIfEmpty(ResponseEntity.noContent().build())
            .block();

        return handleRespons(leveranseRespons);
    }

    /**
     * RESPONS FRA INNSENDING ENDEPUNKT I FPFORDEL
     *  200 -> forsendelse fordelt til GOSYS
     *  202 -> sendt inn, men ikke fordelt enda. Følge redirect 'location' som redirecter til '/status'-endepunktet
     *  303 -> fordelt i FPSAK
     *  ANNET -> Feiltilstand. Vi kan ikke garantere at vi har mottatt noe. Feil hardt.
     */
    private FordelResultat handleRespons(ResponseEntity<FordelKvittering> leveranseRespons) {
        if (leveranseRespons == null || leveranseRespons.getBody() == null) {
            FEILET_KVITTERINGER.increment();
            var httpStatus = leveranseRespons != null ? leveranseRespons.getStatusCode() : null;
            throw new InnsendingFeiletFpFordelException(httpStatus, "Tom respons fra fpfordel. Må sjekkes opp");
        }

        return switch (leveranseRespons.getBody()) {
            case FPSakFordeltKvittering kvittering -> håndterFpsakFordeltKvittering(kvittering);
            case PendingKvittering kvittering -> håndterPendingKvittering(locationFra(leveranseRespons), kvittering);
            case GosysKvittering kvittering -> håndterGosysKvittering(kvittering);
            default -> {
                FEILET_KVITTERINGER.increment();
                throw new InnsendingFeiletFpFordelException(leveranseRespons.getStatusCode() + " Uventet format på kvitteringen mottatt ved innsending av dokument!");
            }
        };
    }

    private static FordelResultat håndterFpsakFordeltKvittering(FPSakFordeltKvittering kvittering) {
        LOG.info("Forsendelse mottatt og fordelt til FPSAK");
        FORDELT_KVITTERING.increment();
        return new FordelResultat(kvittering.getJournalpostId(), kvittering.getSaksnummer());
    }

    private static FordelResultat håndterGosysKvittering(GosysKvittering kvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}", kvittering.getJournalpostId());
        MANUELL_KVITTERING.increment();
        return new FordelResultat(kvittering.getJournalpostId(), null);
    }

    private FordelResultat håndterPendingKvittering(URI pollURI, PendingKvittering pendingKvittering) {
        LOG.info("Søknaden er mottatt, men enda ikke fordelt til FPSAK eller GOSYS. Starter polling på status..");
        for (var i = 1; i <= cfg.maxPollingForsøk(); i++) {
            LOG.info("Poller {} for {}. gang av {}", pollURI, i, cfg.maxPollingForsøk());
            var kvittering = status(pollURI, pendingKvittering.getPollInterval());

            if (kvittering == null || kvittering.getBody() == null) {
                FEILET_KVITTERINGER.increment();
                var httpStatus = kvittering != null ? kvittering.getStatusCode() : null;
                throw new UventetFpFordelResponseException(httpStatus, "Tom respons fra fpfordel ved polling på status.");
            }

            var fordelResultat = switch (kvittering.getBody()) {
                case FPSakFordeltKvittering fpSakFordeltKvittering -> håndterFpsakFordeltKvittering(fpSakFordeltKvittering);
                case GosysKvittering gosysKvittering -> håndterGosysKvittering(gosysKvittering);
                case PendingKvittering pending -> {
                    LOG.info("Fikk pending kvittering på {}. forsøk", i);
                    yield null;
                }
                default -> {
                    FEILET_KVITTERINGER.increment();
                    LOG.warn("Uventet kvitteringer etter leveranse av søknad, gir opp");
                    throw uventetFordelException(kvittering);
                }
            };

            if (fordelResultat != null) {
                return fordelResultat;
            }
        }
        LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", cfg.maxPollingForsøk());
        GITTOPP_KVITTERING.increment();
        throw new UventetFpFordelResponseException("Forsendelser er mottatt, men ikke fordel. ");
    }

    private ResponseEntity<FordelKvittering> status(URI pollingURL, Duration delay) {
        return webClient.get()
            .uri(pollingURL)
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity(FordelKvittering.class)
            .delayElement(delay)
            .retryWhen(retryOnlyOn5xxFailures(cfg.fordelEndpoint().toString()))
            .onErrorResume(e -> {
                FEILET_KVITTERINGER.increment();
                return Mono.error(new UventetFpFordelResponseException(e));
            })
            .block();
    }

    private static URI locationFra(ResponseEntity<FordelKvittering> respons) {
        return Optional.ofNullable(respons.getHeaders().getFirst(LOCATION))
            .map(URI::create)
            .orElseThrow(() -> new UventetFpFordelResponseException("Respons innehold ingen location header for å sjekke på status!"));
    }


    private static UventetFpFordelResponseException uventetFordelException(ResponseEntity<FordelKvittering> leveranseRespons) {
        return new UventetFpFordelResponseException(leveranseRespons.getStatusCode());
    }

    @Override
    public String name() {
        return "fpfordel";
    }

    private static String name(SøknadType type) {
        return type.name().toLowerCase();
    }
}
