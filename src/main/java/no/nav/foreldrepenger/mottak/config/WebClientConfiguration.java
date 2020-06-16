package no.nav.foreldrepenger.mottak.config;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.isDevOrLocal;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static no.nav.foreldrepenger.mottak.util.TokenUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.organisasjon.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSSystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.MDCUtil;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

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
                .codecs(loggingCodec(cfg.isLog()))
                .baseUrl(cfg.getBaseUri())
                .defaultHeaders(h -> h.setBasicAuth(cfg.getUsername(), cfg.getPassword()))
                .build();
    }

    @Qualifier(ARBEIDSFORHOLD)
    @Bean
    public WebClient arbeidsforholdClient(WebClient.Builder builder, ArbeidsforholdConfig cfg,
            ExchangeFilterFunction... filters) {
        builder
                .codecs(loggingCodec(cfg.isLog()))
                .baseUrl(cfg.getBaseUri());
        Arrays.stream(filters).forEach(builder::filter);
        return builder.build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient organisasjonClient(WebClient.Builder builder, OrganisasjonConfig cfg) {
        return builder
                .codecs(loggingCodec(cfg.isLog()))
                .baseUrl(cfg.getBaseUri())
                .filter(loggingFilterFunction())
                .build();
    }

    private Consumer<ClientCodecConfigurer> loggingCodec(boolean log) {
        return c -> c.defaultCodecs().enableLoggingRequestDetails(isDevOrLocal(env) ? log : false);
    }

    @Bean
    ExchangeFilterFunction tokensAddingFilterFunction(STSSystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return (req, next) -> {
            LOG.trace("Bruker token utgår {}", tokenUtil.getExpiration());
            LOG.trace("System token utgår {}", sts.getSystemToken().getExpiration());
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
                    .header(NAV_CONSUMER_ID, consumerId())
                    .header(NAV_CALL_ID1, callId())
                    .build());
        };
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
