package no.nav.foreldrepenger.mottak.http.interceptors;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;

@ConditionalOnK8s
public class TokenExchangeClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TokenExchangeClientRequestInterceptor.class);
    private final ClientConfigurationProperties config;
    private final OAuth2AccessTokenService service;

    public TokenExchangeClientRequestInterceptor(ClientConfigurationProperties config,
            OAuth2AccessTokenService service) {
        this.config = config;
        this.service = service;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        LOG.info("Token exchange intercept call to {} with config {}", request.getURI(), config);
        return execution.execute(request, body);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + ", service=" + service + "]";
    }
}
