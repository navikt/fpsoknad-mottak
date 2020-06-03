package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.slf4j.MarkerFactory.getMarker;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.util.TokenUtil;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    private static final String BEARER = "Bearer ";
    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    @Qualifier("STS")
    public WebClient webClientSTS(@Value("${sts.uri}") String uri, @Value("${kafka.username}") String systemUser,
            @Value("${kafka.password}") String systemPassword) {
        return WebClient
                .builder()
                .baseUrl(uri)
                .defaultHeaders(h -> h.setBasicAuth(systemUser, systemPassword))
                .build();
    }

    @Qualifier("REST")
    @Bean
    public WebClient webClientRest(ExchangeFilterFunction... filters) {
        var builder = WebClient
                .builder()
                .baseUrl("https://modapp-q1.adeo.no/aareg-services/api/v1/arbeidstaker/arbeidsforhold");
        LOG.info("Registrerer {} filtre", filters.length);
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Bean
    ExchangeFilterFunction systemBearerTokenAddingFilterFunction(STSSystemUserTokenService sts) {
        LOG.info("Registrerer system token filter");
        return (req, next) -> {
            LOG.info("Legger til system token");
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_TOKEN, BEARER + sts.getSystemToken().getToken()).build());
        };
    }

    @Bean
    ExchangeFilterFunction navIdentAddingFilterFunction(TokenUtil tokenUtil) {
        LOG.info("Registrerer NAV id filter");
        return (req, next) -> {
            LOG.info(getMarker("CONFIDENTIAL"), "Legger til personinfo {} {}", tokenUtil.getToken(),
                    tokenUtil.autentisertBruker());
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CALL_ID, callId())
                    .header(AUTHORIZATION, BEARER + tokenUtil.getToken())
                    .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                    .build());
        };
    }

    @Bean
    @Order(HIGHEST_PRECEDENCE)
    ExchangeFilterFunction logRequest() {
        LOG.info("Registrerer logging filter");
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            StringBuilder sb = new StringBuilder("Request: \n");
            clientRequest
                    .headers()
                    .forEach(
                            (name, values) -> values.forEach(value -> sb.append(name).append("->").append(values)));
            LOG.info(sb.toString());
            return Mono.just(clientRequest);
        });
    }
}
