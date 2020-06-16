package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;
import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

public abstract class AbstractWebClientConnection implements PingEndpointAware {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractWebClientConnection.class);
    private final WebClient webClient;
    private final String name;
    private final AbstractConfig cfg;

    public AbstractWebClientConnection(WebClient webClient, AbstractConfig cfg, String name) {
        this.webClient = webClient;
        this.name = name;
        this.cfg = cfg;
    }

    protected WebClient getWebClient() {
        return webClient;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String ping() {
        LOG.trace("Pinger {}", pingEndpoint());
        var respons = webClient
                .get()
                .uri(pingEndpoint())
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block()
                .getBody();
        LOG.trace("Pinget {}, fikk {}", pingEndpoint(), respons);
        return respons;
    }

    @Override
    public URI pingEndpoint() {
        return cfg.pingEndpoint();
    }
}
