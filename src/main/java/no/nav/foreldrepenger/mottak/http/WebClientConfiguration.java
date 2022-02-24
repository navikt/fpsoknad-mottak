package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.common.util.Constants.FORELDREPENGER;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID2;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.common.util.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.TokenUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import no.nav.foreldrepenger.mottak.http.interceptors.TokenXConfigFinder;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DKIFConfig;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontonummerConfig;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.TokenUtil;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    private static final String NAV_PERSONIDENTER = "Nav-Personidenter";
    private static final String TEMA = "TEMA";
    public static final String STS = "STS";
    public static final String PDL_USER = "PDL";
    public static final String PDL_SYSTEM = "PDL-RELASJON";
    public static final String KRR = "KRR";
    public static final String ARBEIDSFORHOLD_STS = "ARBEIDSFORHOLD_STS";
    public static final String ARBEIDSFORHOLD_TOKENX = "ARBEIDSFORHOLD_TOKENX";
    public static final String ORGANISASJON = "ORGANISASJON";
    public static final String KONTONR = "KONTONR";

    @Value("${spring.application.name:fpsoknad-mottak}")
    private String consumer;

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    @Qualifier(KRR)
    public WebClient webClientDKIF(Builder builder, DKIFConfig cfg, SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(dkifExchangeFilterFunction(sts, tokenUtil))
                .build();
    }

    @Bean
    @Qualifier(KONTONR)
    public WebClient webClientKontonummer(Builder builder, KontonummerConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(tokenXFilterFunction)
                .build();
    }

    @Bean
    @Qualifier(STS)
    public WebClient webClientSTS(Builder builder, STSConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .defaultHeaders(h -> h.setBasicAuth(cfg.getUsername(), cfg.getPassword()))
                .build();
    }

    @Bean
    @Qualifier(ARBEIDSFORHOLD_STS)
    public WebClient webClientArbeidsforhold(Builder builder, ArbeidsforholdConfig cfg, SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(authenticatingFilterFunction(sts, tokenUtil))
                .build();
    }

    @Bean
    @Qualifier(ARBEIDSFORHOLD_TOKENX)
    public WebClient webClientArbeidsforholdTokenX(Builder builder, ArbeidsforholdConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(tokenXFilterFunction)
                .build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient webClientOrganisasjon(Builder builder, OrganisasjonConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .build();
    }

    @Qualifier(PDL_USER)
    @Bean
    public WebClient webClientPDL(Builder builder, PDLConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(temaFilterFunction())
                .filter(tokenXFilterFunction)
                .build();
    }

    @Qualifier(PDL_SYSTEM)
    @Bean
    public WebClient webClientSystemPDL(Builder builder, PDLConfig cfg, SystemTokenTjeneste sts) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(pdlSystemUserExchangeFilterFunction(sts))
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

    private static ExchangeFilterFunction authenticatingFilterFunction(SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return (req, next) -> {
            var builder = ClientRequest.from(req)
                    .header(NAV_CONSUMER_TOKEN, sts.bearerToken());
            if (tokenUtil.erAutentisert()) {
                return next.exchange(builder
                    .header(AUTHORIZATION, sts.bearerToken())
                    .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                    .build());
            }
            LOG.trace("Uautentisert bruker, kan ikke sette auth headers");
            return next.exchange(builder.build());
        };
    }

    private static ExchangeFilterFunction temaFilterFunction() {
        return (req, next) -> next.exchange(ClientRequest.from(req)
                .header(TEMA, FORELDREPENGER)
                .build());

    }

    private ExchangeFilterFunction dkifExchangeFilterFunction(SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return (req, next) -> next.exchange(ClientRequest.from(req)
                .header(AUTHORIZATION, sts.bearerToken())
                .header(NAV_CONSUMER_ID, consumerId())
                .header(NAV_PERSONIDENTER, tokenUtil.getSubject())
                .build());

    }

    private static ExchangeFilterFunction pdlSystemUserExchangeFilterFunction(SystemTokenTjeneste sts) {
        return (req, next) -> next.exchange(ClientRequest.from(req)
                .header(AUTHORIZATION, sts.bearerToken())
                .header(TEMA, FORELDREPENGER)
                .header(NAV_CONSUMER_TOKEN, sts.bearerToken())
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
        private final TokenXConfigFinder finder;
        private final ClientConfigurationProperties configs;

        TokenXExchangeFilterFunction(ClientConfigurationProperties configs, OAuth2AccessTokenService service, TokenXConfigFinder finder) {
            this.service = service;
            this.finder = finder;
            this.configs = configs;
        }

        @Override
        public Mono<ClientResponse> filter(ClientRequest req, ExchangeFunction next) {
            var url = req.url();
            LOG.trace("Sjekker token exchange for {}", url);
            var config = finder.findProperties(configs, url);
            if (config != null) {
                LOG.trace("Gjør token exchange for {} med konfig {}", url, config);
                var token = service.getAccessToken(config).getAccessToken();
                LOG.info("Token exchange for {} OK", url);
                return next.exchange(ClientRequest.from(req).header(AUTHORIZATION, BEARER + token)
                        .build());
            }
            LOG.trace("Ingen token exchange for {}", url);
            return next.exchange(ClientRequest.from(req).build());
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [service=" + service + ", finder=" + finder + ", configs=" + configs + "]";
        }
    }
}
