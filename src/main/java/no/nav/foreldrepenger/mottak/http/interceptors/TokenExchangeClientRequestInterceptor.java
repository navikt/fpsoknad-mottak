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
    private final ClientConfigurationProperties configs;
    private final OAuth2AccessTokenService service;

    public TokenExchangeClientRequestInterceptor(ClientConfigurationProperties configs,
            OAuth2AccessTokenService service) {
        this.configs = configs;
        this.service = service;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String targetApp = request.getURI().getHost();
        var config = configs.getRegistration().get(targetApp);
        if (config != null) {
            try {
                LOG.info("Veksler inn token for {}", targetApp);
                var token = service.getAccessToken(config);
                LOG.info("Vekslet inn token {} for {}", token.getAccessToken().length(), targetApp);
                // TODO erstatt AUTHORIZATION header
            } catch (Exception e) {
                LOG.warn("OOPS", e);
            }
        } else {
            LOG.info("Ingen konfig for {}", targetApp);
        }
        return execution.execute(request, body);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [service=" + service + "]";
    }
}
