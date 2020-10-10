package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.PersonDTO;
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

    public PersonDTO hentPerson() {
        LOG.info("PDL Henter person");
        var p = client.post("query-person.graphql", Map.of("ident", tokenUtil.getSubject()), PDLPerson.class).block();
        LOG.info("PDL person {}", p);
        var kontonr = kontonr();
        var m = PDLMapper.map(tokenUtil.getSubject(), kontonr, p);
        LOG.info("PDL person mappet til {}", m);
        return m;
    }

    private Bankkonto kontonr() {
        LOG.info("TPS Henter kontonummer fra  {}", cfg.getKontonummerURI());
        var kontonr = getForObject(cfg.getKontonummerURI(), Bankkonto.class);
        LOG.info("TPS kontonummer {}", kontonr);
        return kontonr;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", client=" + client + "]";
    }

    public Navn navn(String id) {
        var n = client.post("query-navn.graphql", Map.of("ident", id), Navn.class).block();
        LOG.info("PDL navn for {} er {}", id, n);
        return n;
    }

}
