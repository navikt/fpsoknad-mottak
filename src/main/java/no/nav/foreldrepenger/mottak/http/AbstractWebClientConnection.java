package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import java.net.URI;

import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

public abstract class AbstractWebClientConnection implements RetryAware, PingEndpointAware {
    protected final WebClient webClient;
    protected final AbstractConfig config;

    protected AbstractWebClientConnection(WebClient webClient, AbstractConfig config) {
        this.webClient = webClient;
        this.config = config;
    }

    @Override
    public String ping() {
        return webClient
            .get()
            .uri(pingEndpoint())
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    @Override
    public URI pingEndpoint() {
        return config.pingEndpoint();
    }
}
