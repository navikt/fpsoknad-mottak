package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.SøknadSender.FPFORDEL_SENDER;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;

@Component
public class FPFordelConnection extends AbstractRestConnection implements Pingable {

    private final FPFordelConfig config;
    private final FPFordelResponseHandler responseHandler;

    public FPFordelConnection(RestOperations restOperations, FPFordelConfig config,
            FPFordelResponseHandler responseHandler) {
        super(restOperations);
        this.config = config;
        this.responseHandler = responseHandler;
    }

    public Kvittering send(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        return responseHandler.handle(postForEntity(uri(), payload, FPFordelKvittering.class));
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return uri(config.getUri(), config.getPingPath());
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    private URI uri() {
        return uri(config.getUri(), config.getBasePath());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + ", responseHandler=" + responseHandler + "]";
    }

    @Override
    public String name() {
        return FPFORDEL_SENDER;
    }

}
