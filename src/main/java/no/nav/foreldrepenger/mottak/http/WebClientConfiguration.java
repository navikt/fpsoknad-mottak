package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID2;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_ID;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

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

import no.nav.foreldrepenger.common.util.MDCUtil;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelConfig;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGeneratorConfig;
import no.nav.foreldrepenger.mottak.oversikt.OversiktConfig;
import reactor.core.publisher.Hooks;


@Configuration
public class WebClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

    public static final String FPFORDEL = "FPFORDEL";
    public static final String FPOVERSIKT = "FPOVERSIKT";
    public static final String PDF_GENERATOR = "PDF_GENERATOR";

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
    @Qualifier(FPOVERSIKT)
    public WebClient webClientOversikt(WebClient.Builder builder, OversiktConfig cfg, TokenXExchangeFilterFunction tokenXFilterFunction) {
        return builder.clone()
                .baseUrl(cfg.getBaseUri().toString())
                .clientConnector(new JdkClientHttpConnector(httpClientProxyEnabled()))
                .filter(correlatingFilterFunction())
                .filter(tokenXFilterFunction)
                .build();
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
