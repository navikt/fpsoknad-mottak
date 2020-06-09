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

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.organisasjon.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Configuration
public class WebClientConfiguration {

    public static final String STS = "STS";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";
    private static final String BEARER = "Bearer ";
    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

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
    public WebClient arbeidsforholdClient(WebClient.Builder builder, ArbeidsforholdConfig config,
            ExchangeFilterFunction... filters) {
        builder
                .exchangeStrategies(exchangeStrategies(config.isLog()))
                .baseUrl(config.getBaseUri());
        LOG.info("Registrerer {} filtre", filters.length);
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient organisasjonClient(WebClient.Builder builder, OrganisasjonConfig config) {
        return builder.exchangeStrategies(exchangeStrategies(config.isLog()))
                .baseUrl(config.getBaseUri())
                .build();
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

    private ExchangeStrategies exchangeStrategies(boolean log) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.withDefaults();
        exchangeStrategies
                .messageWriters().stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .map(LoggingCodecSupport.class::cast)
                .forEach(w -> w.setEnableLoggingRequestDetails(log));
        return exchangeStrategies;
    }
}
