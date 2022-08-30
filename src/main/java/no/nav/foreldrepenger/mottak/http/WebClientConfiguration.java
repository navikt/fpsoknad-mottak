package no.nav.foreldrepenger.mottak.http;

import static no.nav.boot.conditionals.EnvUtil.isDevOrLocal;
import static no.nav.foreldrepenger.common.util.Constants.FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID2;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.common.util.TokenUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.time.Duration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.common.util.MDCUtil;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DigdirKrrProxyConfig;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontonummerConfig;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConfig;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfiguration {

    private static final String TEMA = "TEMA";
    public static final String PDL_USER = "PDL";
    public static final String PDL_SYSTEM = "PDL-RELASJON";
    public static final String KRR = "KRR";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";
    public static final String KONTONR = "KONTONR";
    public static final String KONTOREGISTER = "KONTOREGISTER";

    @Value("${spring.application.name:fpsoknad-mottak}")
    private String consumer;

    @Bean
    public WebClientCustomizer fellesWebKlientKonfig(Environment env) {
        var provider = ConnectionProvider.builder("custom")
            .maxConnections(50)
            .maxIdleTime(Duration.ofSeconds(20))
            .maxLifeTime(Duration.ofSeconds(60))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .evictInBackground(Duration.ofSeconds(120))
            .build();
        return webClientBuilder -> webClientBuilder
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider).wiretap(isDevOrLocal(env))))
            .filter(correlatingFilterFunction())
            .build();
    }


    @Bean
    @Qualifier(KRR)
    public WebClient webClientDigdir(Builder builder, DigdirKrrProxyConfig cfg, TokenUtil tokenUtil, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(navPersonIdentFunction(tokenUtil))
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(KONTONR)
    public WebClient webClientKontonummer(Builder builder, KontoregisterConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(KONTOREGISTER)
    public WebClient webClientKontoregister(Builder builder, KontonummerConfig cfg, ClientConfigurationProperties configs, OAuth2AccessTokenService service) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(azureADClientCredentailFilterFunction("client-credentials-sokos", configs, service))
            .build();
    }

    @Bean
    @Qualifier(ARBEIDSFORHOLD)
    public WebClient webClientArbeidsforholdTokenX(Builder builder,
                                                   ArbeidsforholdConfig cfg,
                                                   TokenUtil tokenUtil,
                                                   TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(navPersonIdentFunction(tokenUtil))
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(ORGANISASJON)
    public WebClient webClientOrganisasjon(Builder builder, OrganisasjonConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .build();
    }

    @Bean
    @Qualifier(PDL_USER)
    public WebClient webClientPDL(Builder builder, PDLConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(temaFilterFunction())
                .filter(tokenXFilterFunction)
                .build();
    }

    @Qualifier(PDL_SYSTEM)
    @Bean
    public WebClient webClientSystemPDL(Builder builder, PDLConfig cfg, ClientConfigurationProperties configs, OAuth2AccessTokenService service) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(temaFilterFunction())
                .filter(azureADClientCredentailFilterFunction("client-credentials-pdl", configs, service))
                .build();
    }

    @Qualifier(PDL_USER)
    @Bean
    public GraphQLWebClient pdlWebClient(@Qualifier(PDL_USER) WebClient client, ObjectMapper mapper) {
        return GraphQLWebClient.newInstance(client, mapper);
    }

    @Qualifier(PDL_SYSTEM)
    @Bean
    public GraphQLWebClient pdlSystemWebClient(@Qualifier(PDL_SYSTEM) WebClient client, ObjectMapper mapper) {
        return GraphQLWebClient.newInstance(client, mapper);
    }

    private static ExchangeFilterFunction temaFilterFunction() {
        return (req, next) -> next.exchange(ClientRequest.from(req)
                .header(TEMA, FORELDREPENGER)
                .build());
    }

    private static ExchangeFilterFunction navPersonIdentFunction(TokenUtil tokenUtil) {
        return (req, next) -> next.exchange(ClientRequest.from(req)
                .header(NAV_PERSON_IDENT, tokenUtil.autentisertBrukerOrElseThrowException().value())
                .build());
    }

    private static ExchangeFilterFunction azureADClientCredentailFilterFunction(String registrering, ClientConfigurationProperties configs, OAuth2AccessTokenService service) {
        return (req, next) -> next.exchange(ClientRequest.from(req)
            .header(AUTHORIZATION, BEARER + service.getAccessToken(configs.getRegistration().get(registrering)).getAccessToken())
            .build());
    }

    private ExchangeFilterFunction correlatingFilterFunction() {
        return (req, next) -> next.exchange(ClientRequest.from(req)
                .header(NAV_CONSUMER_ID, consumerId())
                .header(NAV_CALL_ID, MDCUtil.callId())
                .header(NAV_CALL_ID1, MDCUtil.callId())
                .header(NAV_CALL_ID2, MDCUtil.callId())
                .build());
    }

    private String consumerId() {
        return Optional.ofNullable(MDCUtil.consumerId())
                .orElse(consumer);
    }

    @Component
    public class TokenXExchangeFilterFunction implements ExchangeFilterFunction {

        private static final Logger LOG = LoggerFactory.getLogger(TokenXExchangeFilterFunction.class);

        private final OAuth2AccessTokenService service;
        private final ClientConfigurationPropertiesMatcher matcher;
        private final ClientConfigurationProperties configs;

        TokenXExchangeFilterFunction(ClientConfigurationProperties configs, OAuth2AccessTokenService service, ClientConfigurationPropertiesMatcher matcher) {
            this.service = service;
            this.matcher = matcher;
            this.configs = configs;
        }

        @Override
        public Mono<ClientResponse> filter(ClientRequest req, ExchangeFunction next) {
            var url = req.url();
            LOG.trace("Sjekker token exchange for {}", url);
            var config = matcher.findProperties(configs, url);
            if (config.isPresent()) {
                LOG.trace("Gjør token exchange for {} med konfig {}", url, config);
                var token = service.getAccessToken(config.get()).getAccessToken();
                LOG.info("Token exchange for {} OK", url);
                return next.exchange(ClientRequest.from(req).header(AUTHORIZATION, BEARER + token)
                    .build());
            }
            LOG.trace("Ingen token exchange for {}", url);
            return next.exchange(ClientRequest.from(req).build());
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [service=" + service + ", matcher=" + matcher + ", configs=" + configs + "]";
        }
    }
}
