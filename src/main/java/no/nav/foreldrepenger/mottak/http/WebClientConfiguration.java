package no.nav.foreldrepenger.mottak.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.common.util.MDCUtil;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelConfig;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGeneratorConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DigdirKrrProxyConfig;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConfig;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Hooks;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

import static no.nav.foreldrepenger.common.util.Constants.FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID2;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.http.TokenUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Configuration
public class WebClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    private static final String TEMA = "TEMA";
    public static final String PDL_USER = "PDL";
    public static final String PDL_SYSTEM = "PDL-RELASJON";
    public static final String KRR = "KRR";
    public static final String FPFORDEL = "FPFORDEL";
    public static final String PDF_GENERATOR = "PDF_GENERATOR";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";
    public static final String KONTOREGISTER = "KONTOREGISTER";

    @Value("${spring.application.name:fpsoknad-mottak}")
    private String consumer;

    private static HttpClient httpClientProxyDisabled() {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .proxy(HttpClient.Builder.NO_PROXY)
            .build();
    }

    private static HttpClient httpClientProxyEnabled() {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .proxy(ProxySelector.getDefault())
            .build();
    }

    // global config for å unngå unødig onErrorDropped på Error-nivå
    @EventListener(ApplicationReadyEvent.class)
    public void setupReactorErrorHandler() {
        Hooks.onErrorDropped(e -> LOG.info("onErrorDropped: {}", e.getMessage()));
    }

    @Bean
    @Qualifier(PDF_GENERATOR)
    public WebClient webClientPdfGenerator(WebClient.Builder builder, PdfGeneratorConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .clientConnector(new JdkClientHttpConnector(httpClientProxyDisabled()))
            .filter(correlatingFilterFunction())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(FPFORDEL)
    public WebClient webClientFpfordel(WebClient.Builder builder, FordelConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .clientConnector(new JdkClientHttpConnector(httpClientProxyDisabled()))
            .filter(correlatingFilterFunction())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(KRR)
    public WebClient webClientDigdir(WebClient.Builder builder, DigdirKrrProxyConfig cfg, TokenUtil tokenUtil, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .clientConnector(new JdkClientHttpConnector(httpClientProxyEnabled()))
            .filter(correlatingFilterFunction())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(KONTOREGISTER)
    public WebClient webClientKontoregister(WebClient.Builder builder, KontoregisterConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .clientConnector(new JdkClientHttpConnector(httpClientProxyEnabled()))
            .filter(correlatingFilterFunction())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(ARBEIDSFORHOLD)
    public WebClient webClientArbeidsforholdTokenX(WebClient.Builder builder,
                                                   ArbeidsforholdConfig cfg,
                                                   TokenUtil tokenUtil,
                                                   TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .clientConnector(new JdkClientHttpConnector(httpClientProxyDisabled()))
            .filter(correlatingFilterFunction())
            .filter(navPersonIdentFunction(tokenUtil))
            .filter(tokenXFilterFunction)
            .build();
    }

    @Bean
    @Qualifier(ORGANISASJON)
    public WebClient webClientOrganisasjon(WebClient.Builder builder, OrganisasjonConfig cfg) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .clientConnector(new JdkClientHttpConnector(httpClientProxyDisabled()))
            .filter(correlatingFilterFunction())
            .build();
    }

    @Bean
    @Qualifier(PDL_USER)
    public WebClient webClientPDL(WebClient.Builder builder, PDLConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .defaultHeader(TEMA, FORELDREPENGER)
            .clientConnector(new JdkClientHttpConnector(httpClientProxyDisabled()))
            .filter(correlatingFilterFunction())
            .filter(tokenXFilterFunction)
            .build();
    }

    @Qualifier(PDL_SYSTEM)
    @Bean
    public WebClient webClientSystemPDL(WebClient.Builder builder, PDLConfig cfg, ClientConfigurationProperties configs, OAuth2AccessTokenService service) {
        return builder.clone()
            .baseUrl(cfg.getBaseUri().toString())
            .defaultHeader(TEMA, FORELDREPENGER)
            .clientConnector(new JdkClientHttpConnector(httpClientProxyDisabled()))
            .filter(correlatingFilterFunction())
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
}
