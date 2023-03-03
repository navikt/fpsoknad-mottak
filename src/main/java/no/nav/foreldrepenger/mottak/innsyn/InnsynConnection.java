package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

import static no.nav.foreldrepenger.mottak.http.RetryAwareWebClientConfiguration.retryOnlyOn5xxFailures;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.FPINFO;

@Component
public class InnsynConnection extends AbstractWebClientConnection {
    private static final Logger LOG = LoggerFactory.getLogger(InnsynConnection.class);
    private final InnsynConfig cfg;

    public InnsynConnection(@Qualifier(FPINFO) WebClient client, InnsynConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
    }

    @Override
    public String name() {
        return cfg.name();
    }

    Saker saker(AktørId aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return webClient.get()
            .uri(cfg.sakerURI(aktørId.value()))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Saker.class)
            .retryWhen(retryOnlyOn5xxFailures(cfg.getBaseUri().toString()))
            .defaultIfEmpty(new Saker(Set.of(), Set.of(), Set.of()))
            .block();
    }

    public Optional<AnnenPartVedtak> annenPartVedtak(AnnenPartVedtakRequest annenPartVedtakRequest) {
        return webClient.post()
            .uri(cfg.annenPartVedtakURI())
            .body(Mono.just(annenPartVedtakRequest), AnnenPartVedtakRequest.class)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(AnnenPartVedtak.class)
            .retryWhen(retryOnlyOn5xxFailures(cfg.getBaseUri().toString()))
            .onErrorResume(WebClientResponseException.Forbidden.class, forbidden -> {
                LOG.info("Kall for å hente annenparts vedtak feiler med {}", forbidden.getRawStatusCode(), forbidden);
                return Mono.empty();
            })
            .blockOptional();
    }

    @Override
    public String toString() {
        return "InnsynConnectionWebClient{" +
            "cfg=" + cfg +
            '}';
    }
}
