package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_SYSTEM;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_USER;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLFamilierelasjon.PDLRelasjonsRolle.BARN;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final String BARN_QUERY = "query-barn.graphql";
    private static final String PERSON_QUERY = "query-person.graphql";
    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);
    private final GraphQLWebClient userClient;
    private final GraphQLWebClient systemClient;

    private final TokenUtil tokenUtil;
    private PDLConfig cfg;

    public PDLConnection(@Qualifier(PDL_USER) GraphQLWebClient userClient, @Qualifier(PDL_SYSTEM) GraphQLWebClient systemClient,
            RestOperations restOperations, PDLConfig cfg,
            TokenUtil tokenUtil) {
        super(restOperations);
        this.userClient = userClient;
        this.systemClient = systemClient;
        this.tokenUtil = tokenUtil;
        this.cfg = cfg;
    }

    public PersonDTO hentPerson() {
        var p = oppslag(userClient, PERSON_QUERY, tokenUtil.getSubject(), PDLPerson.class);
        LOG.info("PDL-person {} har relasjoner {}", tokenUtil.getSubject(), p.getFamilierelasjoner());
        var barn = p.getFamilierelasjoner()
                .stream()
                .filter(b -> b.getRelatertPersonrolle().equals(BARN))
                .filter(Objects::nonNull)
                .map(b -> oppslag(systemClient, BARN_QUERY, b.getId(), PDLBarn.class))
                .collect(toSet());
        LOG.info("PDL-person {} har barn {}", tokenUtil.getSubject(), barn);
        var m = PDLMapper.map(tokenUtil.getSubject(), målform(), kontonr(), p);
        LOG.info("PDL person mappet til {}", m);
        return m;
    }

    private static <T> T oppslag(GraphQLWebClient client, String query, String id, Class<T> clazz) {
        LOG.info("PDL oppslag {} med id {}", clazz, id);
        var r = client.post(query, idFra(id), clazz).block();
        LOG.info("PDL oppslag av {} er {}", clazz.getSimpleName(), r);
        return r;
    }

    private static Map<String, Object> idFra(String id) {
        return Map.of("ident", id);
    }

    private Bankkonto kontonr() {
        LOG.info("TPS Henter kontonummer fra  {}", cfg.getKontonummerURI());
        var kontonr = getForObject(cfg.getKontonummerURI(), Bankkonto.class);
        LOG.info("TPS kontonummer {}", kontonr);
        return kontonr;
    }

    private String målform() {
        LOG.info("TPS Henter målform fra  {}", cfg.getMaalformURI());
        var målform = getForObject(cfg.getMaalformURI(), String.class);
        LOG.info("TPS målform {}", målform);
        return målform;
    }

    public Navn navn(String id) {
        var n = userClient.post("query-navn.graphql", Map.of("ident", id), Navn.class).block();
        LOG.info("PDL navn for {} er {}", id, n);
        return n;
    }

}
