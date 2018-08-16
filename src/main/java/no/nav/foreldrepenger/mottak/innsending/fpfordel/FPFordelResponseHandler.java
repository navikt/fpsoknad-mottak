package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.AVSLÅTT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.http.RemoteUnavailableException;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.FPInfoKvittering;
import no.nav.foreldrepenger.mottak.util.EnvUtil;

@Component
public class FPFordelResponseHandler {

    @Inject
    private Environment env;

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final RestTemplate template;
    private final int maxAntallForsøk;

    public FPFordelResponseHandler(RestTemplate template, @Value("${fpfordel.max:5}") int maxAntallForsøk) {
        this.template = template;
        this.maxAntallForsøk = maxAntallForsøk;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> leveranseRespons, String ref) {
        LOG.info("Behandler respons {}", leveranseRespons);
        StopWatch timer = new StopWatch();
        timer.start();
        if (!leveranseRespons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
        FPFordelKvittering fpFordelKvittering = FPFordelKvittering.class.cast(leveranseRespons.getBody());
        switch (leveranseRespons.getStatusCode()) {
        case ACCEPTED:
            if (fpFordelKvittering instanceof FPFordelPendingKvittering) {
                LOG.info("Søknaden er mottatt, men ikke forsøkt behandlet i FPSak");
                FPFordelPendingKvittering pending = FPFordelPendingKvittering.class.cast(leveranseRespons.getBody());
                URI pollURI = locationFra(leveranseRespons);
                for (int i = 1; i <= maxAntallForsøk; i++) {
                    LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                    ResponseEntity<FPFordelKvittering> fpInfoRespons = pollFPFordel(pollURI,
                            pending.getPollInterval().toMillis());
                    fpFordelKvittering = FPFordelKvittering.class.cast(fpInfoRespons.getBody());
                    LOG.info("Behandler poll respons {} etter {}ms", fpInfoRespons, timer.getTime());
                    switch (fpInfoRespons.getStatusCode()) {
                    case OK:
                        if (fpFordelKvittering instanceof FPFordelPendingKvittering) {
                            LOG.info("Fikk pending kvittering  på {}. forsøk", i);
                            pending = FPFordelPendingKvittering.class.cast(fpFordelKvittering);
                            continue;
                        }
                        if (fpFordelKvittering instanceof FPFordelGosysKvittering) {
                            LOG.info("Fikk Gosys kvittering  på {}. forsøk, returnerer etter {}ms", i, stop(timer));
                            return påVentKvittering(ref, FPFordelGosysKvittering.class.cast(fpFordelKvittering));
                        }
                        LOG.warn("Uventet kvittering {} for statuskode {}, gir opp (etter {}ms)", fpFordelKvittering,
                                fpInfoRespons.getStatusCode(), stop(timer));
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    case SEE_OTHER:
                        return pollFPInfo(fpInfoRespons, ref, timer, fpFordelKvittering,
                                pending.getPollInterval().toMillis());
                    default:
                        LOG.warn("Uventet responskode {} etter leveranse av søknad, gir opp (etter {}ms)",
                                fpInfoRespons.getStatusCode(),
                                timer.getTime());
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    }
                }
                LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp (etter {}ms)", maxAntallForsøk,
                        stop(timer));
                return new Kvittering(PÅ_VENT, ref);
            }
        default:
            LOG.warn("Uventet responskode {} ved leveranse av søknad, gir opp (etter {}ms)",
                    leveranseRespons.getStatusCode(),
                    stop(timer));
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private Kvittering pollFPInfo(ResponseEntity<FPFordelKvittering> respons, String ref, StopWatch timer,
            FPFordelKvittering kvittering, long delayMillis) {
        FPSakFordeltKvittering fordeltKvittering = FPSakFordeltKvittering.class.cast(kvittering);
        FPInfoKvittering forsendelsesStatus = pollForsendelsesStatus(locationFra(respons), delayMillis, ref, timer);
        return forsendelsesStatus != null
                ? forsendelsesStatusKvittering(forsendelsesStatus, fordeltKvittering, ref)
                : sendtOgForsøktBehandletKvittering(ref, fordeltKvittering);
    }

    private static long stop(StopWatch timer) {
        timer.stop();
        return timer.getTime();
    }

    private static Kvittering forsendelsesStatusKvittering(FPInfoKvittering forsendelsesStatus,
            FPSakFordeltKvittering fordeltKvittering, String ref) {

        switch (forsendelsesStatus.getForsendelseStatus()) {
        case AVSLÅTT:
            return kvitteringMedType(AVSLÅTT, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case INNVILGET:
            return kvitteringMedType(INNVILGET, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case PÅ_VENT:
            return kvitteringMedType(PÅ_VENT, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        case PÅGÅR:
            return kvitteringMedType(PÅGÅR, ref, fordeltKvittering.getJournalpostId(),
                    fordeltKvittering.getSaksnummer());
        default:
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static URI locationFra(ResponseEntity<FPFordelKvittering> respons) {
        return Optional
                .ofNullable(respons.getHeaders().getFirst(LOCATION))
                .map(URI::create)
                .orElseThrow(IllegalArgumentException::new);
    }

    private FPInfoKvittering pollForsendelsesStatus(URI pollURI, long delayMillis, String ref, StopWatch timer) {
        FPInfoKvittering kvittering = null;
        // https://fpinfo-t10.nais.preprod.local/fpinfo/api/dokumentforsendelse/status?forsendelseId=5aaf77c8-ac56-429f-829a-08de7b060f5b
        // https://app-t10.adeo.no/fpsak/api/dokumentforsendelse/status?forsendelseId=5aaf77c8-ac56-429f-829a-08de7b060f5
        if (EnvUtil.isDevOrPreprod(env) && !pollURI.getHost().contains("fpinfo")) {
            pollURI = rewriteURI(pollURI); // temporary hack until fpinfo behaves as expected
        }

        try {
            for (int i = 1; i <= maxAntallForsøk; i++) {
                LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                ResponseEntity<FPInfoKvittering> respons = pollFPInfo(pollURI, delayMillis);
                if (!respons.hasBody()) {
                    LOG.warn("Fikk ingen kvittering etter polling av forsendelsesstatus");
                    return null;
                }
                kvittering = respons.getBody();
                switch (kvittering.getForsendelseStatus()) {
                case AVSLÅTT:
                case INNVILGET:
                case PÅ_VENT:
                    stop(timer);
                    LOG.info("Sak har status {} etter {}ms", kvittering.getForsendelseStatus().name().toLowerCase(),
                            timer.getTime());
                    return kvittering;
                case PÅGÅR:
                    LOG.info("Sak pågår fremdeles etter {}ms", timer.getTime());
                    continue;
                default:
                    LOG.info("Dette skal ikke skje");
                }
            }
            stop(timer);
            return kvittering;
        } catch (RemoteUnavailableException e) {
            stop(timer);
            LOG.warn("Kunne ikke sjekke status for forsendelse på {}", pollURI, e);
            return null;
        }
    }

    private static URI rewriteURI(URI pollURI) {
        try {
            URIBuilder builder = new URIBuilder(pollURI);
            builder.setHost("fpinfo");
            builder.setScheme("http");
            builder.setPath("/fpinfo/api/dokumentforsendelse/");
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(pollURI).build().getQueryParams();
            builder.setParameter("status", queryParams.getFirst("status"));
            URI rewrittenURI = builder.build();
            LOG.info("Rewriting pollURI from {} to {}", pollURI, rewrittenURI);
            return rewrittenURI;
        } catch (URISyntaxException e) {
            return pollURI;
        }

    }

    private ResponseEntity<FPInfoKvittering> pollFPInfo(URI pollURI, long delayMillis) {
        return poll(pollURI, "FPInfo", delayMillis, FPInfoKvittering.class);
    }

    private ResponseEntity<FPFordelKvittering> pollFPFordel(URI uri, long delayMillis) {
        return poll(uri, "FPFordel", delayMillis, FPFordelKvittering.class);
    }

    private <T> ResponseEntity<T> poll(URI uri, String name, long delayMillis, Class<T> clazz) {
        try {
            waitFor(delayMillis);
            return template.getForEntity(uri, clazz);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle {} på {}", name, uri, e);
            throw new RemoteUnavailableException(uri, e);
        }
    }

    private static void waitFor(long delayMillis) throws InterruptedException {
        LOG.trace("Venter i {}ms", delayMillis);
        Thread.sleep(delayMillis);
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type, ref);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

    private static Kvittering påVentKvittering(String ref, FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}",
                gosysKvittering.getJournalpostId());
        return kvitteringMedType(PÅ_VENT, ref, gosysKvittering.getJournalpostId(), null);
    }

    private static Kvittering sendtOgForsøktBehandletKvittering(String ref, FPSakFordeltKvittering kvittering) {
        LOG.info("Søknaden er motatt og forsøkt behandlet av FPSak, journalId er {}, saksnummer er {}",
                kvittering.getJournalpostId(), kvittering.getSaksnummer());
        return kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK, ref, kvittering.getJournalpostId(),
                kvittering.getSaksnummer());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
