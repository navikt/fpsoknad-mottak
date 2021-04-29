package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import no.nav.foreldrepenger.mottak.http.interceptors.ClientPropertiesFinder;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import reactor.core.publisher.Mono;

class TokenXExchangeFilterFunction implements ExchangeFilterFunction {

    private static final Logger LOG = LoggerFactory.getLogger(TokenXExchangeFilterFunction.class);

    private final OAuth2AccessTokenService service;
    private final ClientPropertiesFinder finder;
    private final ClientConfigurationProperties configs;

    TokenXExchangeFilterFunction(ClientConfigurationProperties configs, OAuth2AccessTokenService service,
            no.nav.foreldrepenger.mottak.http.interceptors.ClientPropertiesFinder finder) {
        this.service = service;
        this.finder = finder;
        this.configs = configs;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest req, ExchangeFunction next) {
        var config = finder.findProperties(configs, req.url());
        if (config != null) {
            LOG.trace("Exchanging for {}", req.url());
            return next.exchange(ClientRequest.from(req).header(AUTHORIZATION + "Bearer ", service.getAccessToken(config).getAccessToken())
                    .build());
        }
        LOG.trace("No exchanging for {}", req.url());
        return next.exchange(ClientRequest.from(req).build());
    }
}