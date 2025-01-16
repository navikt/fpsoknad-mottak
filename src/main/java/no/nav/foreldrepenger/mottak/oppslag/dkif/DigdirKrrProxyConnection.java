package no.nav.foreldrepenger.mottak.oppslag.dkif;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KRR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.http.Retry;
import reactor.core.publisher.Mono;

@Component
public class DigdirKrrProxyConnection {
    private static final Logger LOG = LoggerFactory.getLogger(DigdirKrrProxyConnection.class);
    private final WebClient webClient;
    private final DigdirKrrProxyConfig cfg;

    public DigdirKrrProxyConnection(@Qualifier(KRR) WebClient client, DigdirKrrProxyConfig cfg) {
        this.webClient = client;
        this.cfg = cfg;
    }

    public Målform målform() {
        try {
            LOG.info("Henter målform fra digdir-krr-proxy");
            return hentMålform();
        } catch (Exception e) {
            LOG.warn("DKIF oppslag målform feilet. Bruker default Målform", e);
            return Målform.standard();
        }
    }

    @Retry
    private Målform hentMålform() {
        return webClient.get()
            .uri(uri -> cfg.kontaktUri())
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Kontaktinformasjon.class)
            .mapNotNull(Kontaktinformasjon::målform)
            .onErrorResume(e -> {
                LOG.warn("DKIF oppslag målform feilet. Bruker default Målform", e);
                return Mono.empty();
            })
            .defaultIfEmpty(Målform.standard())
            .timeout(Duration.ofSeconds(3))
            .block();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [cfg=" + cfg + "]";
    }

}
