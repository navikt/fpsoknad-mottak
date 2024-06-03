package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.common.util.TokenUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher;
import reactor.core.publisher.Mono;

@Component
public class TokenXExchangeFilterFunction implements ExchangeFilterFunction {
    private static final Logger LOG = LoggerFactory.getLogger(TokenXExchangeFilterFunction.class);
    private final OAuth2AccessTokenService service;
    private final ClientConfigurationPropertiesMatcher matcher;
    private final ClientConfigurationProperties configs;
    private final TokenUtil tokenUtil;
    TokenXExchangeFilterFunction(ClientConfigurationProperties configs, OAuth2AccessTokenService service, ClientConfigurationPropertiesMatcher matcher, TokenUtil tokenUtil) {
        this.service = service;
        this.matcher = matcher;
        this.configs = configs;
        this.tokenUtil = tokenUtil;
    }
    @Override
    public Mono<ClientResponse> filter(ClientRequest req, ExchangeFunction next) {
        var url = req.url();
        var urlUtenQueryParam = url.toString().split("\\?")[0];
        LOG.trace("Sjekker token exchange for {}", urlUtenQueryParam);
        var config = matcher.findProperties(configs, url);

        if (config != null && tokenUtil.erAutentisert()) {
            LOG.trace("Gjør token exchange for {} med konfig {}", urlUtenQueryParam, config);
            var token = service.getAccessToken(config).getAccessToken();
            LOG.info("Token exchange for {} OK", urlUtenQueryParam);
            return next.exchange(ClientRequest.from(req).header(AUTHORIZATION, BEARER + token)
                .build());
        }
        LOG.trace("Ingen token exchange for {}", urlUtenQueryParam);
        return next.exchange(ClientRequest.from(req).build());
    }
    @Override
    public String toString() {
        return getClass().getSimpleName() + " [service=" + service + ", matcher=" + matcher + ", configs=" + configs + "]";
    }
}
