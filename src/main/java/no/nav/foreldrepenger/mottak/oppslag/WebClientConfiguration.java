package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.organisasjon.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Configuration
public class WebClientConfiguration {

    public static final String STS = "STS";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";
    private static final String BEARER = "Bearer ";

    @Value("${spring.application.name:fpsoknad-mottak}")
    String consumer;

    @Bean
    @Qualifier(STS)
    public WebClient webClientSTS(WebClient.Builder builder, @Value("${sts.uri}") String uri,
            @Value("${kafka.username}") String systemUser,
            @Value("${kafka.password}") String systemPassword) {
        return builder
                .baseUrl(uri)
                .defaultHeaders(h -> h.setBasicAuth(systemUser, systemPassword))
                .build();
    }

    @Qualifier(ARBEIDSFORHOLD)
    @Bean
    public WebClient arbeidsforholdClient(WebClient.Builder builder, ArbeidsforholdConfig cfg,
            ExchangeFilterFunction... filters) {
        builder.exchangeStrategies(loggingEnablingStrategy(cfg.isLog()))
                .baseUrl(cfg.getBaseUri());
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient organisasjonClient(WebClient.Builder builder, OrganisasjonConfig cfg) {
        return builder.exchangeStrategies(loggingEnablingStrategy(cfg.isLog()))
                .baseUrl(cfg.getBaseUri())
                .filter(loggingFilterFunction())
                .build();
    }

    @Bean
    ExchangeFilterFunction tokenAddingFilterFunction(STSSystemUserTokenService sts, TokenUtil tokenUtil) {
        return (req, next) -> {
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_TOKEN, BEARER + sts.getSystemToken().getToken())
                    .header(AUTHORIZATION, tokenUtil.bearerToken())
                    .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                    .build());
        };
    }

    @Bean
    ExchangeFilterFunction loggingFilterFunction() {
        return (req, next) -> {
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_ID, consumer)
                    .header(NAV_CALL_ID1, callId())
                    .build());
        };
    }

    private final ExchangeStrategies loggingEnablingStrategy(boolean log) {
        ExchangeStrategies strategies = ExchangeStrategies.withDefaults();
        strategies.messageWriters()
                .stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .map(LoggingCodecSupport.class::cast)
                .forEach(w -> w.setEnableLoggingRequestDetails(log));
        return strategies;
    }
}
