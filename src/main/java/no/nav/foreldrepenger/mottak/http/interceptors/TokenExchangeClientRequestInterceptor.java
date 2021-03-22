package no.nav.foreldrepenger.mottak.http.interceptors;

import java.io.IOException;
import java.net.URI;

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
    private final ClientConfigurationProperties configs;
    private final OAuth2AccessTokenService service;
    private final ClientPropertiesFinder mapper;

    public TokenExchangeClientRequestInterceptor(ClientConfigurationProperties configs,
            OAuth2AccessTokenService service, ClientPropertiesFinder mapper) {
        this.configs = configs;
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();
        var config = mapper.findProperties(configs, uri);
        if (config != null) {
            try {
                LOG.info("Veksler inn token for {}", uri);
                var token = service.getAccessToken(config);
                LOG.info("Vekslet inn token {} for {}", token.getAccessToken().length(), uri);
                // TODO erstatt AUTHORIZATION header
            } catch (Exception e) {
                LOG.warn("OOPS", e);
            }
        } else {
            LOG.info("Ingen konfig for {}", uri);
        }
        return execution.execute(request, body);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [service=" + service + "]";
    }
}
