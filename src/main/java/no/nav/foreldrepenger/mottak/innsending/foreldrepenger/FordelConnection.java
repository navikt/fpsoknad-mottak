package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.util.CounterRegistry.FP_SENDFEIL;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FordelKvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;

@Component
public class FordelConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(FordelConnection.class);

    private final FordelConfig cfg;
    private final ResponseHandler handler;

    public FordelConnection(RestOperations restOperations, FordelConfig cfg,
            ResponseHandler responseHandler) {
        super(restOperations, cfg);
        this.cfg = cfg;
        this.handler = responseHandler;
    }

    public FordelResultat send(Konvolutt konvolutt) {
        try {
            LOG.info("Sender {} til {}", name(konvolutt.getType()), name());
            var kvittering = handler.handle(post(konvolutt));
            LOG.info("Sendte {} til {}, fikk kvittering {}", name(konvolutt.getType()), name(), kvittering);
            return kvittering;
        } catch (Exception e) {
            LOG.info("Feil ved sending av {}", konvolutt.getMetadata());
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
