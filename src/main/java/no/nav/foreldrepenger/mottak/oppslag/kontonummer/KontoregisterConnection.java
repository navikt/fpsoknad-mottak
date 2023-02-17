package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import static no.nav.foreldrepenger.common.util.MDCUtil.callId;
import static no.nav.foreldrepenger.mottak.http.RetryAwareWebClientConfiguration.retryOnlyOn5xxFailures;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KONTOREGISTER;
import static no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.Konto.UKJENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.Konto;
import reactor.core.publisher.Mono;

@Component
public class KontoregisterConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(KontoregisterConnection.class);
    private final KontoregisterConfig cfg;

    public KontoregisterConnection(@Qualifier(KONTOREGISTER) WebClient client, KontoregisterConfig cfg) {
        super(client, cfg);
        this.cfg = cfg;
    }

    public Konto kontonrFraNyTjeneste() {
        LOG.info("Henter kontonummer fra {}", cfg.kontoregisterURI());
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
//            .retryWhen(retryOnlyOn5xxFailures(cfg.kontoregisterURI().toString()))
            .doOnError(throwable -> LOG.info("Oppslag av kontonummer feilet! Forsetter uten kontonummer!", throwable))
            .onErrorReturn(UKJENT)
            .defaultIfEmpty(UKJENT)
            .block();
    }

    @Override
    public String name() {
        return capitalize(KONTOREGISTER).toLowerCase();
    }
}
