package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.AVSLÅTT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;

@Component
public class FPFordelResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final RestTemplate template;
    private final int maxAntallForsøk;

    public FPFordelResponseHandler(RestTemplate template, @Value("${fpfordel.max:3}") int maxAntallForsøk) {
        this.template = template;
        this.maxAntallForsøk = maxAntallForsøk;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> respons, String ref) {
        LOG.info("Behandler respons {}", respons);
        if (!respons.hasBody()) {
            LOG.warn("Fikk ingen kvittering etter leveranse av søknad");
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }

        FPFordelKvittering kvittering = FPFordelKvittering.class.cast(respons.getBody());
        switch (respons.getStatusCode()) {
        case ACCEPTED:
            if (kvittering instanceof FPFordelPendingKvittering) {
                LOG.info("Søknaden er mottatt, men ikke forsøkt behandlet i FPSak");
                FPFordelPendingKvittering pendingKvittering = FPFordelPendingKvittering.class.cast(respons.getBody());
                URI pollURI = locationFra(respons);
                for (int i = 1; i <= maxAntallForsøk; i++) {
                    LOG.info("Poller {} for {}. gang av {}", pollURI, i, maxAntallForsøk);
                    respons = poll(pollURI, ref, pendingKvittering);
                    kvittering = FPFordelKvittering.class.cast(respons.getBody());
                    LOG.info("Behandler respons {} ({})", respons, i);
                    switch (respons.getStatusCode()) {
                    case OK:
                        if (kvittering instanceof FPFordelPendingKvittering) {
                            LOG.info("Søknaden er mottatt av FPFordel, men ikke forsøkt behandlet i FPSak");
                            pendingKvittering = FPFordelPendingKvittering.class.cast(kvittering);
                            continue;
                        }
                        if (kvittering instanceof FPFordelGosysKvittering) {
                            return påVentKvittering(ref, FPFordelGosysKvittering.class.cast(kvittering));
                        }
                        LOG.warn("Uventet kvittering {} for statuskode {}", kvittering, respons.getStatusCode());
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    case SEE_OTHER:
                        FPSakKvittering saksStatus = sjekkStatus(locationFra(respons), ref);
                        if (saksStatus != null) {
                            return saksStatusKvittering(ref, saksStatus);
                        }
                        FPSakFordeltKvittering fordelt = FPSakFordeltKvittering.class.cast(kvittering);
                        return sendtOgForsøktBehandletKvittering(ref, fordelt.getJournalpostId(),
                                fordelt.getSaksnummer());

                    default:
                        LOG.warn("Uventet responskode {} etter leveranse av søknad", respons.getStatusCode());
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    }
                }
                LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", maxAntallForsøk);
                return new Kvittering(PÅ_VENT, ref);
            }
        default:
            LOG.warn("Uventet responskode {} fra leveranse av søknad", respons.getStatusCode());
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static Kvittering saksStatusKvittering(String ref, FPSakKvittering saksStatus) {
        switch (saksStatus.getForsendelseStatus()) {
        case AVSLÅTT:
            return kvitteringMedType(AVSLÅTT, ref, saksStatus.getJournalpostId(),
                    saksStatus.getSaksnummer());
        case INNVILGET:
            return kvitteringMedType(INNVILGET, ref, saksStatus.getJournalpostId(),
                    saksStatus.getSaksnummer());
        case PÅ_VENT:
            return kvitteringMedType(PÅ_VENT, ref, saksStatus.getJournalpostId(),
                    saksStatus.getSaksnummer());
        case PÅGÅR:
            return kvitteringMedType(PÅGÅR, ref, saksStatus.getJournalpostId(),
                    saksStatus.getSaksnummer());
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

    private FPSakKvittering sjekkStatus(URI uri, String ref) {
        try {
            LOG.info("Sjekker forsendelsesstatus på {}", uri);
            return template.getForEntity(uri, FPSakKvittering.class).getBody();
        } catch (Exception e) {
            LOG.warn("Kunne ikke sjekke status for forsendelse på {}", uri, e);
            return null;
        }
    }

    private ResponseEntity<FPFordelKvittering> poll(URI uri, String ref, FPFordelPendingKvittering pendingKvittering) {
        try {
            LOG.info("Venter i {}ms", pendingKvittering.getPollInterval().toMillis());
            Thread.sleep(pendingKvittering.getPollInterval().toMillis());
            return template.getForEntity(uri, FPFordelKvittering.class);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle FPFordel på {}", uri, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    private static Kvittering kvitteringMedType(LeveranseStatus type, String ref, String journalId, String saksnr) {
        Kvittering kvittering = new Kvittering(type, ref);
        kvittering.setJournalId(journalId);
        kvittering.setSaksNr(saksnr);
        return kvittering;
    }

    private static Kvittering påVentKvittering(String ref, FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys, journalId er {}", gosysKvittering.getJounalId());
        return kvitteringMedType(PÅ_VENT, ref, gosysKvittering.getJounalId(), null);
    }

    private static Kvittering sendtOgForsøktBehandletKvittering(String ref, String journalId, String saksnr) {
        LOG.info("Søknaden er motatt og forsøkt behandlet av FPSak, journalId er {}, saksnummer er {}",
                journalId, saksnr);
        return kvitteringMedType(SENDT_OG_FORSØKT_BEHANDLET_FPSAK, ref, journalId, saksnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
