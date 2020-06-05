package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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
        return WebClient
                .builder()
                .baseUrl(uri)
                .defaultHeaders(h -> h.setBasicAuth(systemUser, systemPassword))
                .build();
    }

    @Qualifier("REST")
    @Bean
    public WebClient arbeidsforholdClient(ArbeidsforholdConfig config, ExchangeFilterFunction... filters) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.withDefaults();
        exchangeStrategies
                .messageWriters().stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .map(LoggingCodecSupport.class::cast)
                .forEach(w -> w.setEnableLoggingRequestDetails(config.isLog()));

        var builder = WebClient
                .builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(config.getBaseUri());
        LOG.info("Registrerer {} filtre", filters.length);
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Bean
    ExchangeFilterFunction systemBearerTokenAddingFilterFunction(STSSystemUserTokenService sts, TokenUtil tokenUtil,
            @Value("${spring.application.name:fpsoknad-mottak}") String consumer) {
        return (req, next) -> {
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_ID, consumer)
                    .header(NAV_CONSUMER_TOKEN, BEARER + sts.getSystemToken().getToken())
                    .header(NAV_CALL_ID1, callId())
                    .header(AUTHORIZATION, tokenUtil.bearerToken())
                    .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                    .build());
        };
    }
}
