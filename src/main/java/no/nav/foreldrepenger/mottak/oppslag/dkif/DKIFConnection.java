package no.nav.foreldrepenger.mottak.oppslag.dkif;

import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.KRR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.mottak.http.AbstractWebClientConnection;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

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
        LOG.info("Henter målform for {}", tokenUtil.fnr());
        var mf = getWebClient().get()
                .uri(b -> cfg.kontaktUri(b))
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntityList(DigitalKontaktinfo.class)
                .block()
                .getBody()
                .stream()
                .map(d -> d.getMålform(tokenUtil.getSubject()))
                .findFirst()
                .orElse(Målform.def());
        LOG.info("Henter målform {}", mf);
        return mf;
    }

    @Override
    public String name() {
        return capitalize(KRR.toLowerCase());
    }

}
