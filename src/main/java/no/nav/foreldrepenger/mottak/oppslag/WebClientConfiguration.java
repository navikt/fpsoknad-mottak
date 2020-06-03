package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Configuration
public class WebClientConfiguration {

    private static final String BEARER = "Bearer ";
    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    @Qualifier("STS")
    public WebClient webClientSTS(@Value("${sts.uri}") String uri, @Value("${kafka.username}") String systemUser,
            @Value("${kafka.password}") String systemPassword) {
        return WebClient.builder().baseUrl(uri)
                .defaultHeaders(h -> h.setBasicAuth(systemUser, systemPassword))
                .build();
    }

    @Qualifier("REST")
    @Bean
    public WebClient webClientRest(ExchangeFilterFunction... filters) {
        var builder = WebClient.builder();
        LOG.info("Legger til {} filtre", filters.length);
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Bean
    ExchangeFilterFunction systemBearerTokenAddingFilterFunction(STSSystemUserTokenService sts) {
        return (req, next) -> {
            LOG.info("Legger til system token");
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_TOKEN, BEARER + sts.getSystemToken().getToken()).build());
        };
    }

    @Bean
    ExchangeFilterFunction navIdentAddingFilterFunction(TokenUtil tokenUtil) {
        return (req, next) -> {
            LOG.info("CONFIDENTIAL", "Legger til personinfo {} {}", tokenUtil.getToken(),
                    tokenUtil.autentisertBruker());
            return next.exchange(ClientRequest.from(req)
                    .header(AUTHORIZATION, BEARER + tokenUtil.getToken())
                    .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                    .build());
        };
    }
}
