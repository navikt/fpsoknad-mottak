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
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelConfig;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGeneratorConfig;
import no.nav.foreldrepenger.mottak.innsyn.InnsynConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DigdirKrrProxyConfig;
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
    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    private static final String TEMA = "TEMA";
    public static final String PDL_USER = "PDL";
    public static final String PDL_SYSTEM = "PDL-RELASJON";
    public static final String KRR = "KRR";
    public static final String FPINFO = "FPINFO";
    public static final String FPFORDEL = "FPFORDEL";
    public static final String PDF_GENERATOR = "PDF_GENERATOR";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";
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
    @Qualifier(PDF_GENERATOR)
    public WebClient webClientPdfGenerator(Builder builder, PdfGeneratorConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(FPFORDEL)
    public WebClient webClientFpfordel(Builder builder, FordelConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(FPINFO)
    public WebClient webClientFpinfo(Builder builder, InnsynConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(tokenXFilterFunction)
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
    @Qualifier(KONTOREGISTER)
    public WebClient webClientKontoregister(Builder builder, KontoregisterConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(tokenXFilterFunction)
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
                .defaultHeader(TEMA, FORELDREPENGER)
                .filter(tokenXFilterFunction)
                .build();
    }

    @Qualifier(PDL_SYSTEM)
    @Bean
    public WebClient webClientSystemPDL(Builder builder, PDLConfig cfg, ClientConfigurationProperties configs, OAuth2AccessTokenService service) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .defaultHeader(TEMA, FORELDREPENGER)
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

    @Bean
    public ClientConfigurationPropertiesMatcher tokenxClientConfigMatcher() {
        return (properties, uri) -> {
            LOG.trace("Oppslag token X konfig for {}", uri.getHost());
            var cfg = properties.getRegistration().get(uri.getHost().split("\\.")[0]);
            if (cfg != null) {
                LOG.trace("Oppslag token X konfig for {} OK", uri.getHost());
            } else {
                LOG.trace("Oppslag token X konfig for {} fant ingenting", uri.getHost());
            }
            return Optional.ofNullable(cfg);
        };
    }

}
