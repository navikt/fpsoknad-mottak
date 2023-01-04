package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.http.RetryAwareWebClientConfiguration.retryOnlyOn5xxFailures;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.FPINFO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.innsyn.v2.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.common.innsyn.v2.Saksnummer;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.innsyn.dto.BehandlingDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.LenkeDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.SakDTO;
import no.nav.foreldrepenger.mottak.innsyn.dto.UttaksplanDTO;
import reactor.core.publisher.Mono;

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

    List<SakDTO> saker(String aktørId) {
        LOG.trace("Henter saker for {}", aktørId);
        return webClient.get()
            .uri(cfg.sakURI(aktørId))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(SakDTO.class)
            .retryWhen(retryOnlyOn5xxFailures(cfg.getBaseUri().toString()))
            .collectList()
            .blockOptional()
            .orElse(List.of());
    }

    Saker sakerV2(AktørId aktørId) {
        LOG.trace("Henter sakerV2 for {}", aktørId);
        return webClient.get()
            .uri(cfg.sakV2URI(aktørId.value()))
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

    Optional<UttaksplanDTO> uttaksplan(Saksnummer saksnummer) {
        LOG.trace("Henter uttaksplan");
        return webClient.get()
            .uri(cfg.uttaksplanURI(saksnummer).toString())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(UttaksplanDTO.class)
            .retryWhen(retryOnlyOn5xxFailures(cfg.getBaseUri().toString()))
            .blockOptional();
    }

    Optional<UttaksplanDTO> uttaksplan(AktørId aktørId, AktørId annenPart) {
        LOG.trace("Henter uttaksplan for {} med annen part {}", aktørId, annenPart);
        return webClient.get()
            .uri(cfg.uttaksplanURI(aktørId, annenPart).toString())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(UttaksplanDTO.class)
            .retryWhen(retryOnlyOn5xxFailures(cfg.getBaseUri().toString()))
            .doOnError(throwable -> LOG.warn("Kunne ikke hente uttaksplan for annen part {}", annenPart, throwable))
            .onErrorResume(error -> Mono.empty())
            .blockOptional();
    }

    BehandlingDTO behandling(LenkeDTO lenke) {
        return hentBehandlingFraLenke(lenke);
    }

    private BehandlingDTO hentBehandlingFraLenke(LenkeDTO lenke) {
        return Optional.ofNullable(lenke)
            .map(LenkeDTO::href)
            .map(this::hentBehandlingFraLenke)
            .orElse(null);
    }

    private BehandlingDTO hentBehandlingFraLenke(String href) {
        return webClient.get()
            .uri(cfg.createLink(href).toString())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(BehandlingDTO.class)
            .retryWhen(retryOnlyOn5xxFailures(cfg.createLink(href).toString()))
            .block();
    }

    @Override
    public String toString() {
        return "InnsynConnectionWebClient{" +
            "cfg=" + cfg +
            '}';
    }
}
