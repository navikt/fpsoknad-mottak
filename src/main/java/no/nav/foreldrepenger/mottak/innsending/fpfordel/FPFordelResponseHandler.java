package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_MOTATT_FPSAK;
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
                LOG.info("Søknaden er mottatt, men ikke behandlet i FPSak");
                FPFordelPendingKvittering pendingKvittering = FPFordelPendingKvittering.class.cast(respons.getBody());
                URI location = location(respons);
                for (int i = 1; i <= maxAntallForsøk; i++) {
                    LOG.info("Poller {} for {}. gang av {}", location, i, maxAntallForsøk);
                    respons = poll(location, ref, pendingKvittering);
                    kvittering = FPFordelKvittering.class.cast(respons.getBody());
                    LOG.info("Behandler respons {} ({})", respons, i);
                    switch (respons.getStatusCode()) {
                    case OK:
                        if (kvittering instanceof FPFordelPendingKvittering) {
                            LOG.info("Søknaden er mottatt, men ikke behandlet i FPSak");
                            pendingKvittering = FPFordelPendingKvittering.class.cast(kvittering);
                            continue;
                        }
                        if (kvittering instanceof FPFordelGosysKvittering) {
                            return gosysKvittering(ref, FPFordelGosysKvittering.class.cast(kvittering));
                        }
                        if (kvittering instanceof FPFordelKvittering) {
                            return sendtOgMotattKvittering(ref, FPSakFordeltKvittering.class.cast(kvittering));
                        }
                    case SEE_OTHER:
                        return sendtOgMotattKvittering(ref, FPSakFordeltKvittering.class.cast(kvittering));
                    default:
                        LOG.warn("Uventet responskode {} etter leveranse av søknad", respons.getStatusCode());
                        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
                    }
                }
                LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", maxAntallForsøk);
                return new Kvittering(SENDT_FPSAK, ref);
            }
        default:
            LOG.warn("Uventet responskode {} fra leveranse av søknad", respons.getStatusCode());
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private static URI location(ResponseEntity<FPFordelKvittering> respons) {
        return Optional
                .ofNullable(respons.getHeaders().getFirst(LOCATION))
                .map(URI::create)
                .orElseThrow(IllegalArgumentException::new);
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

    private static Kvittering gosysKvittering(String ref, FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys");
        Kvittering kvittering = new Kvittering(SENDT_GOSYS, ref);
        kvittering.setJournalId(gosysKvittering.getJounalId());
        return kvittering;
    }

    private static Kvittering sendtOgMotattKvittering(String ref, FPSakFordeltKvittering fordeltKvittering) {
        LOG.info("Søknaden er motatt og behandlet av FPSak, journalId er {}, saksnummer er {}",
                fordeltKvittering.getJounalId(), fordeltKvittering.getSaksnummer());
        Kvittering kvittering = new Kvittering(SENDT_OG_MOTATT_FPSAK, ref);
        kvittering.setJournalId(fordeltKvittering.getJounalId());
        kvittering.setSaksNr(fordeltKvittering.getSaksnummer());
        return kvittering;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
