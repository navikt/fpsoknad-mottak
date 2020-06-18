package no.nav.foreldrepenger.mottak.config;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.TokenUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.organisasjon.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSSystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.MDCUtil;
import no.nav.foreldrepenger.mottak.util.TokenUtil;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration implements EnvironmentAware {

    public static final String STS = "STS";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";

    @Value("${spring.application.name:fpsoknad-mottak}")
    private String consumer;
    private Environment env;

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    @Qualifier(STS)
    public WebClient webClientSTS(WebClient.Builder builder, STSConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri())
                .filter(logRequestFilterFunction(cfg.isLog()))
                .filter(correlatingFilterFunction())
                .defaultHeaders(h -> h.setBasicAuth(cfg.getUsername(), cfg.getPassword()))
                .build();
    }

    @Qualifier(ARBEIDSFORHOLD)
    @Bean
    public WebClient arbeidsforholdClient(WebClient.Builder builder, ArbeidsforholdConfig cfg,
            ExchangeFilterFunction... filters) {
        builder
                .baseUrl(cfg.getBaseUri());
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient organisasjonClient(WebClient.Builder builder, OrganisasjonConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri())
                .filter(logRequestFilterFunction(cfg.isLog()))
                .filter(correlatingFilterFunction())
                .build();
    }

    @Bean
    ExchangeFilterFunction tokensAddingFilterFunction(STSSystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return (req, next) -> {
            LOG.trace("System token utgår {}", sts.getSystemToken().getExpiration());
            var builder = ClientRequest.from(req)
                    .header(NAV_CONSUMER_TOKEN, BEARER + sts.getSystemToken().getToken());
            if (tokenUtil.erAutentisert()) {
                LOG.trace("Bruker token utgår {}", tokenUtil.getExpiration());
                return next.exchange(
                        builder.header(AUTHORIZATION, tokenUtil.bearerToken())
                                .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                                .build());

            }
            LOG.trace("Uautentisert bruker");
            return next.exchange(builder.build());
        };
    }

    ExchangeFilterFunction correlatingFilterFunction() {
        return (req, next) -> {
            LOG.trace("Legger på call og consumer id for {}", req.url());
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_ID, consumerId())
                    .header(NAV_CALL_ID1, MDCUtil.callId())
                    .build());
        };
    }

    @Bean
    ExchangeFilterFunction logRequestFilterFunction(boolean isLog) {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            LOG.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> LOG.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private String consumerId() {
        return Optional.ofNullable(MDCUtil.consumerId())
                .orElse(consumer);
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
