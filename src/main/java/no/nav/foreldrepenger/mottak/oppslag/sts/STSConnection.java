package no.nav.foreldrepenger.mottak.oppslag.sts;

import static no.nav.foreldrepenger.mottak.config.WebClientConfiguration.STS;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;

@Component
public class STSConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(STSConnection.class);
    private final STSConfig cfg;

    public STSConnection(@Qualifier(STS) WebClient webClient,
            @Value("${spring.application.name:fpsoknad-mottak}") String name, STSConfig cfg) {
        super(webClient, cfg, name);
        this.cfg = cfg;
    }

    SystemToken refresh() {
        return getWebClient().get()
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SystemToken.class)
                .block();
    }

    public Duration getSlack() {
        return cfg.getSlack();
    }
}
