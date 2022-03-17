package no.nav.foreldrepenger.mottak.oppslag.dkif;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KRR_TOKENX;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import reactor.core.publisher.Mono;

@Component
public class DigdirKrrProxyConnection extends AbstractWebClientConnection {
    private static final Logger LOG = LoggerFactory.getLogger(DigdirKrrProxyConnection.class);
    private final DigdirKrrProxyConfig cfg;

    public DigdirKrrProxyConnection(@Qualifier(KRR_TOKENX) WebClient client, DigdirKrrProxyConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
    }

    public Målform målform() {
        LOG.info("Henter målform fra digdir-krr-proxy");
        return webClient.get()
            .uri(uri -> cfg.kontaktUri())
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), clientResponse -> Mono.empty())
            .bodyToMono(Kontaktinformasjon.class)
            .mapNotNull(Kontaktinformasjon::målform)
            .defaultIfEmpty(Målform.standard())
            .block();
    }

    @Override
    public String name() {
        return capitalize(KRR_TOKENX.toLowerCase());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [cfg=" + cfg + "]";
    }

}
