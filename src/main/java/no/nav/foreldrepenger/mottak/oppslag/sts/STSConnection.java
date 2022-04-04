package no.nav.foreldrepenger.mottak.oppslag.sts;

import static no.nav.foreldrepenger.mottak.http.RetryAwareWebClient.retrySpec;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.STS;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;

@Component
public class STSConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(STSConnection.class);
    private final STSConfig cfg;

    public STSConnection(@Qualifier(STS) WebClient webClient, STSConfig cfg) {
        super(webClient, cfg);
        this.cfg = cfg;
    }

    SystemToken refresh() {
        LOG.trace("Refresh av system token");
        var token = webClient
            .post()
            .uri(cfg::getStsURI)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(cfg.stsBody())
            .retrieve()
            .bodyToMono(SystemToken.class)
            .retryWhen(retrySpec(cfg.getStsPath()))
            .block();
        if (token == null) {
            throw new IllegalStateException("Tom body fra STS-tjenesten ble returnert. Noe er galt!");
        }
        LOG.trace("Refresh av system token OK ({})", token.getExpiration());
        return token;
    }

    public Duration getSlack() {
        return cfg.getSlack();
    }

    @Override
    public String name() {
        return "STS";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + "]";
    }

}
