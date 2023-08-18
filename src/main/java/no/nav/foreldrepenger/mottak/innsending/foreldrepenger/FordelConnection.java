package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.util.CounterRegistry.FEILET_KVITTERINGER;
import static no.nav.foreldrepenger.common.util.CounterRegistry.FORDELT_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.FP_SENDFEIL;
import static no.nav.foreldrepenger.common.util.CounterRegistry.GITTOPP_KVITTERING;
import static no.nav.foreldrepenger.common.util.CounterRegistry.MANUELL_KVITTERING;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.FPFORDEL;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_MIXED;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
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
import no.nav.foreldrepenger.mottak.http.Retry;
import reactor.core.publisher.Mono;

@Component
public class FordelConnection {

    private static final Logger LOG = LoggerFactory.getLogger(FordelConnection.class);
    private final WebClient webClient;
    private final FordelConfig cfg;

    protected FordelConnection(@Qualifier(FPFORDEL) WebClient webClient, FordelConfig cfg) {
        this.webClient = webClient;
        this.cfg = cfg;
    }

    public FordelResultat send(Konvolutt konvolutt) {
        try {
            LOG.info("Sender {} til {}", name(konvolutt.getType()), name());
            var kvittering = sendSøknad(konvolutt);
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(konvolutt.getType()), name(), kvittering);
            return kvittering;
        } catch (UventetPollingStatusFpFordelException e) {
            throw e;
        } catch (Exception e) {
            LOG.info("Feil ved sending av {}", konvolutt.getMetadata());
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    @Retry
    private FordelResultat sendSøknad(Konvolutt konvolutt) {
        var leveranseRespons = webClient.post()
            .uri(cfg.fordelEndpoint())
            .contentType(MULTIPART_MIXED)
            .bodyValue(konvolutt.getPayload())
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity(FordelKvittering.class)
            .doOnRequest(va -> konvolutt.getType().count()) // Skal kjøres hver gang, uavhengig om OK elelr feilet respons!
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
        var body = leveranseRespons.getBody();
        if (body instanceof FPSakFordeltKvittering kvittering) {
            return håndterFpsakFordeltKvittering(kvittering);
        }
        if (body instanceof PendingKvittering kvittering) {
            return håndterPendingKvittering(locationFra(leveranseRespons), kvittering);
        }
        if (body instanceof GosysKvittering kvittering) {
            return håndterGosysKvittering(kvittering);
        }
        FEILET_KVITTERINGER.increment();
        throw new InnsendingFeiletFpFordelException(leveranseRespons.getStatusCode() + " Uventet format på kvitteringen mottatt ved innsending av dokument!");
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
                throw new UventetPollingStatusFpFordelException(httpStatus, "Tom respons fra fpfordel ved polling på status.");
            }

            var fordelResultat = oversett(kvittering, i);

            if (fordelResultat != null) {
                return fordelResultat;
            }
        }
        LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", cfg.maxPollingForsøk());
        GITTOPP_KVITTERING.increment();
        throw new UventetPollingStatusFpFordelException("Forsendelser er mottatt, men ikke fordel. ");
    }

    @Nullable
    private static FordelResultat oversett(ResponseEntity<FordelKvittering> response, int forsøk) {
        var body = response.getBody();
        if (body instanceof FPSakFordeltKvittering kvittering) {
            return håndterFpsakFordeltKvittering(kvittering);
        }
        else if (body instanceof GosysKvittering kvittering) {
            return håndterGosysKvittering(kvittering);
        } else if (body instanceof PendingKvittering) {
            LOG.info("Fikk pending kvittering på {}. forsøk", forsøk);
            return null;
        } else {
            FEILET_KVITTERINGER.increment();
            throw new UventetPollingStatusFpFordelException(response.getStatusCode(), "Uventet kvitteringer etter leveranse av søknad, gir opp");
        }
    }

    @Retry
    private ResponseEntity<FordelKvittering> status(URI pollingURL, Duration delay) {
        return webClient.get()
            .uri(pollingURL)
            .accept(APPLICATION_JSON)
            .retrieve()
            .toEntity(FordelKvittering.class)
            .delayElement(delay)
            .onErrorResume(e -> {
                FEILET_KVITTERINGER.increment();
                return Mono.error(new UventetPollingStatusFpFordelException(e));
            })
            .block();
    }

    private static URI locationFra(ResponseEntity<FordelKvittering> respons) {
        return Optional.ofNullable(respons.getHeaders().getFirst(LOCATION))
            .map(URI::create)
            .orElseThrow(() -> new UventetPollingStatusFpFordelException("Respons innehold ingen location header for å sjekke på status!"));
    }

    private static String name() {
        return "fpfordel";
    }

    private static String name(SøknadType type) {
        return type.name().toLowerCase();
    }
}
