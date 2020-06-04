package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.withDefaults();
        exchangeStrategies
                .messageWriters().stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .forEach(writer -> ((LoggingCodecSupport) writer).setEnableLoggingRequestDetails(true));

        var builder = WebClient
                .builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl("https://modapp-q1.adeo.no/aareg-core/api/v1/arbeidstaker/arbeidsforhold?historikk=true");
        LOG.info("Registrerer {} filtre", filters.length);
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Bean
    ExchangeFilterFunction systemBearerTokenAddingFilterFunction(STSSystemUserTokenService sts, TokenUtil tokenUtil) {
        LOG.info("Registrerer system token filter");
        return (req, next) -> {
            LOG.info(MarkerFactory.getMarker("CONFIDENTIAL"), "Legger til system token {}",
                    sts.getSystemToken().getToken());
            LOG.info(MarkerFactory.getMarker("CONFIDENTIAL"), "Legger til user token {}", tokenUtil.getToken());

            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_ID, "fpsoknad-mottak")
                    .header(NAV_CONSUMER_TOKEN, BEARER + sts.getSystemToken().getToken())
                    .header(NAV_CALL_ID1, callId())
                    .header(AUTHORIZATION, BEARER + tokenUtil.getToken())
                    .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                    .build());
        };
    }

    @Bean
    @Order(LOWEST_PRECEDENCE)
    ExchangeFilterFunction logRequest() {
        LOG.info("Registrerer logging filter");
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            clientRequest
                    .headers()
                    .forEach((k, v) -> LOG.info(k + "->" + v));
            LOG.info("URL " + clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
}
