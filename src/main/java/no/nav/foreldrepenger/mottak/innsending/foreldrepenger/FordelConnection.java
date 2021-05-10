package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.ikkeSendt;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_SENDFEIL;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@Component
public class FordelConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(FordelConnection.class);

    private final FordelConfig cfg;
    private final ResponseHandler handler;

    public FordelConnection(/* @Qualifier(TOKENX) */RestOperations restOperations, FordelConfig cfg,
            ResponseHandler responseHandler) {
        super(restOperations, cfg);
        this.cfg = cfg;
        this.handler = responseHandler;
    }

    public Kvittering send(Konvolutt konvolutt) {
        if (isEnabled()) {
            return doSend(konvolutt);
        }
        LOG.info("Sending av {} er deaktivert, ingenting å sende", konvolutt.getType());
        return ikkeSendt(konvolutt.PDFHovedDokument());
    }

    private Kvittering doSend(Konvolutt konvolutt) {
        try {
            LOG.info("Sender {} til {}", name(konvolutt.getType()), name());
            var kvittering = handler.handle(post(konvolutt));
            kvittering.setPdf(konvolutt.PDFHovedDokument());
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(konvolutt.getType()), name(), kvittering);
            return kvittering;
        } catch (Exception e) {
            LOG.info("Feil ved sending av {}", konvolutt.getMetadata());
            LOG.info("Hoveddokument {}", konvolutt.XMLHovedDokument());
            LOG.info("Hoveddokument PDF {}", konvolutt.PDFHovedDokument());
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    private ResponseEntity<FordelKvittering> post(Konvolutt konvolutt) {
        var respons = postForEntity(fordelEndpoint(), konvolutt.getPayload(), FordelKvittering.class);
        konvolutt.getType().count();
        return respons;
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return cfg.pingEndpoint();
    }

    public boolean isEnabled() {
        return cfg.isEnabled();
    }

    @Override
    public String name() {
        return "fpfordel";
    }

    private static String name(SøknadType type) {
        return type.name().toLowerCase();
    }

    private URI fordelEndpoint() {
        return cfg.fordelEndpoint();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + cfg + ", responseHandler=" + handler + "]";
    }
}
