package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KONTONR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;

@Component
public class KontonummerConnection extends AbstractWebClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(KontonummerConnection.class);

    public KontonummerConnection(@Qualifier(KONTONR) WebClient client, KontonummerConfig cfg) {
        super(client, cfg);
    }

    public Bankkonto kontonr() {
        LOG.info("Henter kontonummer");
        return webClient
            .get()
            .uri(cfg.getBaseUri())
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Bankkonto.class)
            .block();
    }

    @Override
    public String name() {
        return capitalize(KONTONR).toLowerCase();
    }
}
