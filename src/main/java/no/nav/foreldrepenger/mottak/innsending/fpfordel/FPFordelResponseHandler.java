package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_MOTATT_FPSAK;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.net.URI;
import java.util.concurrent.TimeUnit;

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

    public Kvittering handle(ResponseEntity<FPFordelKvittering> kvittering, String ref) {
        return handle(kvittering, ref, maxAntallForsøk);
    }

    private Kvittering handle(ResponseEntity<FPFordelKvittering> respons, String ref, int forsøk) {
        LOG.info("Behandler respons {}", respons);
        if (!respons.hasBody()) {
            LOG.warn("Fikk ingen kvittering");
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
        switch (respons.getStatusCode()) {
        case ACCEPTED:
            return poll(respons, ref, forsøk);
        case SEE_OTHER:
            return sendtOgMotattKvittering(ref, FPSakFordeltKvittering.class.cast(respons.getBody()));
        case OK:
            FPFordelKvittering kvittering = FPFordelKvittering.class.cast(respons.getBody());
            if (kvittering instanceof FPFordelPendingKvittering) {
                LOG.info("Poller igen");
                return poll(respons, ref, forsøk);
            }
            if (kvittering instanceof FPSakFordeltKvittering) {
                return sendtOgMotattKvittering(ref, FPSakFordeltKvittering.class.cast(kvittering));
            }
            if (kvittering instanceof FPFordelGosysKvittering) {
                return gosysKvittering(ref, FPFordelGosysKvittering.class.cast(respons.getBody()));
            }
        default:
            LOG.warn("Fikk uventet response kode {}", respons.getStatusCode());
            return new Kvittering(FP_FORDEL_MESSED_UP, ref);
        }
    }

    private Kvittering poll(ResponseEntity<FPFordelKvittering> respons, String ref, int forsøk) {

        String location = respons.getHeaders().getFirst(LOCATION);
        if (location != null) {
            LOG.info("Fikk location header {}", location);
            return pollForsøk(URI.create(location), ref, pollDuration(respons), MILLISECONDS, forsøk);
        }
        LOG.info("Fikk ikke den forventede location headeren");
        return new Kvittering(FP_FORDEL_MESSED_UP, ref);
    }

    private static long pollDuration(ResponseEntity<FPFordelKvittering> respons) {
        return FPFordelPendingKvittering.class.cast(respons.getBody()).getPollInterval().toMillis();
    }

    private Kvittering pollForsøk(URI uri, String ref, long duration, TimeUnit unit, int forsøk) {
        LOG.info("Søknaden er mottatt, men ikke behandlet i FPSak");
        if (forsøk > 0) {
            return poll(uri, ref, unit.toMillis(duration), forsøk - 1);
        }
        LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", maxAntallForsøk);
        return new Kvittering(SENDT_FPSAK, ref);
    }

    private Kvittering poll(URI uri, String ref, long durationMs, int forsøk) {
        try {
            LOG.info("Venter i {}ms", durationMs);
            Thread.sleep(durationMs);
            LOG.info("Poller {} for {}. gang av {}", uri, maxAntallForsøk - forsøk, maxAntallForsøk);
            return handle(template.getForEntity(uri, FPFordelKvittering.class), ref, forsøk);
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
