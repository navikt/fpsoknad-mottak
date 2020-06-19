package no.nav.foreldrepenger.mottak.config;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_TOKEN;
import static no.nav.foreldrepenger.mottak.Constants.NAV_PERSON_IDENT;
import static no.nav.foreldrepenger.mottak.util.TokenUtil.BEARER;
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

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConfig;
import no.nav.foreldrepenger.mottak.oppslag.organisasjon.OrganisasjonConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.STSConfig;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.MDCUtil;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Configuration
public class WebClientConfiguration {

    public static final String STS = "STS";
    public static final String ARBEIDSFORHOLD = "ARBEIDSFORHOLD";
    public static final String ORGANISASJON = "ORGANISASJON";

    @Value("${spring.application.name:fpsoknad-mottak}")
    private String consumer;

    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    @Bean
    @Qualifier(STS)
    public WebClient webClientSTS(WebClient.Builder builder, STSConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri())
                .filter(correlatingFilterFunction())
                .defaultHeaders(h -> h.setBasicAuth(cfg.getUsername(), cfg.getPassword()))
                .build();
    }

    @Qualifier(ARBEIDSFORHOLD)
    @Bean
    public WebClient arbeidsforholdClient(WebClient.Builder builder, ArbeidsforholdConfig cfg,
            ExchangeFilterFunction... filters) {
        return builder
                .filters((f) -> asList(filters))
                .baseUrl(cfg.getBaseUri()).build();
    }

    @Qualifier(ORGANISASJON)
    @Bean
    public WebClient organisasjonClient(WebClient.Builder builder, OrganisasjonConfig cfg) {
        return builder
                .baseUrl(cfg.getBaseUri())
                .filter(correlatingFilterFunction())
                .build();
    }

    @Bean
    ExchangeFilterFunction authenticatingFilterFunction(SystemTokenTjeneste sts, TokenUtil tokenUtil) {
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

    @Bean
    ExchangeFilterFunction correlatingFilterFunction() {
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
