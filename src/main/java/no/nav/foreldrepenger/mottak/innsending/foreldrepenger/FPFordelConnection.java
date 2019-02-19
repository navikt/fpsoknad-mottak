package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

@Component
public class FPFordelConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConnection.class);

    private final FPFordelConfig config;
    private final FPFordelResponseHandler responseHandler;

    public FPFordelConnection(RestOperations restOperations, FPFordelConfig config,
            FPFordelResponseHandler responseHandler) {
        super(restOperations);
        this.config = config;
        this.responseHandler = responseHandler;
    }

    public Kvittering send(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        LOG.info("Sender {} til FPFordel", type.name().toLowerCase());
        Kvittering kvittering = responseHandler.handle(
                postForEntity(uri(config.getUri(), config.getBasePath()), payload, FPFordelKvittering.class));
        LOG.info("Sendte {} til FPFordel, fikk kvittering {}", type.name().toLowerCase(), kvittering);
        type.count();
        return kvittering;
    }

    @Override
    public String ping() {
        URI pingEndpoint = pingEndpoint();
        LOG.info("Pinger {}", pingEndpoint);
        return ping(pingEndpoint);
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String name() {
        return "FPFORDEL";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + ", responseHandler=" + responseHandler + "]";
    }
}
