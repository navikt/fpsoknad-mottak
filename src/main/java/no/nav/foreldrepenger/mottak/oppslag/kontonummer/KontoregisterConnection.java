package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import static no.nav.foreldrepenger.common.util.MDCUtil.callId;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KONTOREGISTER;
import static no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.Konto.UKJENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.Retry;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.Konto;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class KontoregisterConnection {

    private static final Logger LOG = LoggerFactory.getLogger(KontoregisterConnection.class);
    private final WebClient webClient;
    private final KontoregisterConfig cfg;

    public KontoregisterConnection(@Qualifier(KONTOREGISTER) WebClient client, KontoregisterConfig cfg) {
        this.webClient = client;
        this.cfg = cfg;
    }

    public Konto kontonummer() {
        try {
            LOG.info("Henter kontonummer fra {}", cfg.kontoregisterURI());
            return hentKontonummer();
        } catch (Exception e) {
            LOG.info("Oppslag av kontonummer feilet! Forsetter uten kontonummer!", e);
            return UKJENT;
        }
    }

    @Retry
    public Konto hentKontonummer() {
        return webClient.get()
            .uri(cfg.kontoregisterURI())
            .accept(APPLICATION_JSON)
            .header("nav-call-id", callId())
            .retrieve()
            .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                clientResponse -> {
                    LOG.info("Det er ikke registert kontonummer på person!");
                    return Mono.empty();
                })
            .bodyToMono(Konto.class)
            .timeout(Duration.ofSeconds(5))
            .defaultIfEmpty(UKJENT)
            .block();
    }
}
