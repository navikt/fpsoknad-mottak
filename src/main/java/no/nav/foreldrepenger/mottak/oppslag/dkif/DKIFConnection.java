package no.nav.foreldrepenger.mottak.oppslag.dkif;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KRR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

import java.util.List;


@Component
public class DKIFConnection extends AbstractWebClientConnection {
    private static final Logger LOG = LoggerFactory.getLogger(DKIFConnection.class);

    private final DKIFConfig cfg;
    private final TokenUtil tokenUtil;

    public DKIFConnection(@Qualifier(KRR) WebClient client, DKIFConfig cfg, TokenUtil tokenUtil) {
        super(client, cfg);
        this.cfg = cfg;
        this.tokenUtil = tokenUtil;
    }

    public Målform målform() {
        LOG.info("Henter målform");
        return webClient.get()
            .uri(cfg::kontaktUri)
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::isError, ClientResponse::createException)
            .bodyToMono(new ParameterizedTypeReference<List<DigitalKontaktinfo>>() {})
            .blockOptional().orElse(List.of())
            .stream()
            .map(dk -> dk.getMålform(tokenUtil.getSubject()))
            .findFirst()
            .orElse(Målform.standard());
    }

    @Override
    public String name() {
        return capitalize(KRR.toLowerCase());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [cfg=" + cfg + ", tokenUtil=" + tokenUtil + "]";
    }

}
