package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
public class PDLConnection extends AbstractRestConnection {

    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);
    private final GraphQLWebClient client;
    private final TokenUtil tokenUtil;
    private PDLConfig cfg;

    public PDLConnection(GraphQLWebClient client, RestOperations restOperations, PDLConfig cfg, TokenUtil tokenUtil) {
        super(restOperations);
        this.client = client;
        this.tokenUtil = tokenUtil;
        this.cfg = cfg;
    }

    public PDLPerson hentPerson() {
        LOG.info("PDL Henter person");
        var p = client.post("query-person.graphql", Map.of("ident", tokenUtil.getSubject()), PDLPerson.class).block();
        LOG.info("PDL person {}", p);
        var kontonr = kontonr();
        LOG.info("PDL kontonummer {}", kontonr);

        return p;
    }

    private String kontonr() {
        LOG.info("PDL Henter kontonummer fra  {}", cfg.getKontonummerURI());
        return getForObject(cfg.getKontonummerURI(), String.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", client=" + client + "]";
    }

}
