package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.util.Constants.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_PERSON_IDENT;
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
import no.nav.foreldrepenger.mottak.http.interceptors.ClientPropertiesFinder;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DKIFConfig;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontonummerConfig;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.MDCUtil;
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
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
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
    public WebClient webClientKontonummer(Builder builder, KontonummerConfig cfg, TokenUtil tokenUtil,
            TokenXExchangeFilterFunction tokenXFilterFunction) {
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

    @Qualifier(ARBEIDSFORHOLD)
    @Bean
    public WebClient webClientArbeidsforhold(Builder builder, ArbeidsforholdConfig cfg, SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(authenticatingFilterFunction(sts, tokenUtil))
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
    public WebClient webClientPDL(Builder builder, PDLConfig cfg, SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(pdlUserExchangeFilterFunction(sts, tokenUtil))
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
                return next.exchange(
                        builder.header(AUTHORIZATION, tokenUtil.bearerToken())
                                .header(NAV_PERSON_IDENT, tokenUtil.autentisertBruker())
                                .build());

            }
            LOG.trace("Uautentisert bruker, kan ikke sette auth headers");
            return next.exchange(builder.build());
        };
    }

    private static ExchangeFilterFunction authenticatingFilterFunction(TokenUtil tokenUtil) {
        return (req, next) -> {
            var builder = ClientRequest.from(req);
            if (tokenUtil.erAutentisert()) {
                return next.exchange(
                        builder.header(AUTHORIZATION, tokenUtil.bearerToken())
                                .build());

            }
            LOG.trace("Uautentisert bruker, kan ikke sette auth headers");
            return next.exchange(builder.build());
        };
    }

    private static ExchangeFilterFunction pdlUserExchangeFilterFunction(SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return (req, next) -> next.exchange(ClientRequest.from(req)
                .header(AUTHORIZATION, tokenUtil.bearerToken())
                .header(TEMA, FORELDREPENGER)
                .header(NAV_CONSUMER_TOKEN, sts.bearerToken())
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
                .header(NAV_CALL_ID1, MDCUtil.callId())
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
        private final ClientPropertiesFinder finder;
        private final ClientConfigurationProperties configs;

        TokenXExchangeFilterFunction(ClientConfigurationProperties configs, OAuth2AccessTokenService service,
                no.nav.foreldrepenger.mottak.http.interceptors.ClientPropertiesFinder finder) {
            this.service = service;
            this.finder = finder;
            this.configs = configs;
        }

        @Override
        public Mono<ClientResponse> filter(ClientRequest req, ExchangeFunction next) {
            LOG.trace("Sjekker token exchange for {}", req.url());
            var config = finder.findProperties(configs, req.url());
            if (config != null) {
                LOG.trace("Gjør token exchange for {} med konfig {}", req.url(), config);
                return next.exchange(ClientRequest.from(req).header(AUTHORIZATION + "Bearer ", service.getAccessToken(config).getAccessToken())
                        .build());
            }
            LOG.trace("Ingen token exchange for {}", req.url());
            return next.exchange(ClientRequest.from(req).build());
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [service=" + service + ", finder=" + finder + ", configs=" + configs + "]";
        }
    }
}
