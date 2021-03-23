package no.nav.foreldrepenger.mottak.http.interceptors;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;
import no.nav.foreldrepenger.boot.conditionals.EnvUtil;
import no.nav.foreldrepenger.mottak.util.StringUtil;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;

@ConditionalOnK8s
public class TokenExchangeClientRequestInterceptor implements ClientHttpRequestInterceptor, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(TokenExchangeClientRequestInterceptor.class);
    private final ClientConfigurationProperties configs;
    private final OAuth2AccessTokenService service;
    private final ClientPropertiesFinder finder;
    private Environment env;

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
            try {
                LOG.info("Veksler inn token for {}", uri);
                var token = service.getAccessToken(config).getAccessToken();
                LOG.info("Vekslet inn token {} for {}", token.length(), uri);
                if (EnvUtil.isDevOrLocal(env)) {
                    LOG.info("Nytt token {}", StringUtil.limit(token), 50);
                    LOG.info("Gammelt token i header {}", StringUtil.limit(request.getHeaders().getFirst("Authorization")), 50);

                }
                request.getHeaders().setBearerAuth(token);
                if (EnvUtil.isDevOrLocal(env)) {
                    LOG.info("Nytt token i header {}", StringUtil.limit(request.getHeaders().getFirst("Authorization")), 50);
                }
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

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;

    }
}
