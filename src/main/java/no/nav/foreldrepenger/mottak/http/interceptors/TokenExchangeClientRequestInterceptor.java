package no.nav.foreldrepenger.mottak.http.interceptors;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;
import no.nav.foreldrepenger.boot.conditionals.EnvUtil;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;

@ConditionalOnK8s
@Order(HIGHEST_PRECEDENCE)
public class TokenExchangeClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TokenExchangeClientRequestInterceptor.class);
    private final ClientConfigurationProperties configs;
    private final OAuth2AccessTokenService service;
    private final ClientPropertiesFinder finder;

    public TokenExchangeClientRequestInterceptor(ClientConfigurationProperties configs,
            OAuth2AccessTokenService service, ClientPropertiesFinder finder) {
        this.configs = configs;
        this.service = service;
        this.finder = finder;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();
        var config = finder.findProperties(configs, uri);
        if (config != null) {
                var token = service.getAccessToken(config).getAccessToken();
                request.getHeaders().setBearerAuth(token);
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
