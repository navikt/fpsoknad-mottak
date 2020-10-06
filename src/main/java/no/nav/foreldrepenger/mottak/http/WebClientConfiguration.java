package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.util.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.Constants.TEMA;
import static no.nav.foreldrepenger.mottak.util.TokenUtil.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;
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
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemToken;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.MDCUtil;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Configuration
public class WebClientConfiguration {

    public static final String STS = "STS";
    public static final String PDL = "PDL";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";

    @Value("${spring.application.name:fpsoknad-mottak}")
    private String consumer;

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    @Qualifier(STS)
    public WebClient webClientSTS(Builder builder, STSConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri())
                .filter(correlatingFilterFunction())
                .defaultHeaders(h -> h.setBasicAuth(cfg.getUsername(), cfg.getPassword()))
                .build();
    }

    @Qualifier(ARBEIDSFORHOLD)
    @Bean
    public WebClient arbeidsforholdClient(Builder builder, ArbeidsforholdConfig cfg,
            ExchangeFilterFunction... filters) {
        LOG.info("Registrerer {} filtre ({})", filters.length, Arrays.toString(filters));
        var b = builder.baseUrl(cfg.getBaseUri());
        Arrays.stream(filters).forEach(f -> b.filter(f));
        return b.build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient organisasjonClient(Builder builder, OrganisasjonConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri())
                .filter(correlatingFilterFunction())
                .build();
    }

    @Qualifier(PDL)
    @Bean
    public WebClient pdlClient(Builder builder, PDLConfig cfg, SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return builder
                .baseUrl(cfg.getBaseUri())
                .filter(correlatingFilterFunction())
                .filter(pdlExchangeFilterFunction(sts.getSystemToken(), tokenUtil))
                .build();
    }

    @Bean
    public GraphQLWebClient PDLWebClient(@Qualifier(PDL) WebClient client, ObjectMapper mapper) {
        return GraphQLWebClient.newInstance(client, mapper);
    }

    @Bean
    public ExchangeFilterFunction authenticatingFilterFunction(SystemTokenTjeneste sts, TokenUtil tokenUtil) {
        return (req, next) -> {
            LOG.trace("Legger på headerverdier i {} {} {}  for {}", NAV_CONSUMER_TOKEN, AUTHORIZATION, NAV_PERSON_IDENT,
                    req.url());
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

    private static ExchangeFilterFunction pdlExchangeFilterFunction(SystemToken systemToken, TokenUtil tokenUtil) {
        return (req, next) -> {
            LOG.trace("Legger på headerverdier i {} {} {}  for {}", NAV_CONSUMER_TOKEN, AUTHORIZATION, "TEMA",
                    req.url());
            var builder = ClientRequest.from(req)
                    .header(NAV_CONSUMER_TOKEN, BEARER + systemToken.getToken());
            return next.exchange(
                    builder.header(AUTHORIZATION, tokenUtil.bearerToken())
                            .header("TEMA", TEMA)
                            .build());
        };
    }

    @Bean
    public ExchangeFilterFunction correlatingFilterFunction() {
        return (req, next) -> {
            LOG.trace("Legger på call og consumer id for {}", req.url());
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
