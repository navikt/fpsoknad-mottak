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
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.MDCUtil;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Configuration
public class WebClientConfiguration {

    public static final String STS = "STS";
    public static final String PDL_USER = "PDL";
    public static final String PDL_SYSTEM = "PDL-RELASJON";

    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";

    @Value("${spring.application.name:fpsoknad-mottak}")
    private String consumer;

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

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
    public WebClient arbeidsforholdClient(Builder builder, ArbeidsforholdConfig cfg, SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(authenticatingFilterFunction(sts, tokenUtil))
                .build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient organisasjonClient(Builder builder, OrganisasjonConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .build();
    }

    @Qualifier(PDL_USER)
    @Bean
    public WebClient pdlClient(Builder builder, PDLConfig cfg, SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(pdlUserExchangeFilterFunction(sts, tokenUtil))
                .build();
    }

    @Qualifier(PDL_SYSTEM)
    @Bean
    public WebClient pdlClientRelasjon(Builder builder, PDLConfig cfg, SystemTokenTjeneste sts) {
        return builder
                .baseUrl(cfg.getBaseUri().toString())
                .filter(correlatingFilterFunction())
                .filter(pdlSystemUserExchangeFilterFunction(sts))
                .build();
    }

    @Bean
    @Qualifier(PDL_USER)
    public GraphQLWebClient PDLWebClient(@Qualifier(PDL_USER) WebClient client, ObjectMapper mapper) {
        return GraphQLWebClient.newInstance(client, mapper);
    }

    @Bean
    @Qualifier(PDL_SYSTEM)
    public GraphQLWebClient PDLSystemWebClient(@Qualifier(PDL_SYSTEM) WebClient client, ObjectMapper mapper) {
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

    private static ExchangeFilterFunction pdlUserExchangeFilterFunction(SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return (req, next) -> {
            return next.exchange(ClientRequest.from(req)
                    .header(AUTHORIZATION, tokenUtil.bearerToken())
                    .header("TEMA", FORELDREPENGER)
                    .header(NAV_CONSUMER_TOKEN, sts.bearerToken())
                    .build());
        };
    }

    private static ExchangeFilterFunction pdlSystemUserExchangeFilterFunction(SystemTokenTjeneste sts) {
        return (req, next) -> {
            return next.exchange(ClientRequest.from(req)
                    .header(AUTHORIZATION, sts.bearerToken())
                    .header("TEMA", FORELDREPENGER)
                    .header(NAV_CONSUMER_TOKEN, sts.bearerToken())
                    .build());
        };
    }

    private ExchangeFilterFunction correlatingFilterFunction() {
        return (req, next) -> {
            return next.exchange(ClientRequest.from(req)
                    .header(NAV_CONSUMER_ID, consumerId())
                    .header(NAV_CALL_ID1, MDCUtil.callId())
                    .build());
        };
    }

    private String consumerId() {
        return Optional.ofNullable(MDCUtil.consumerId())
                .orElse(consumer);
    }

}
