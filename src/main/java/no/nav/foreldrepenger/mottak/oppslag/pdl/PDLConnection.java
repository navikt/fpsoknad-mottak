package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_SYSTEM;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_USER;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle.BARN;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
public class PDLConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final String NAVN_QUERY = "query-navn.graphql";
    private static final String BARN_QUERY = "query-barn.graphql";
    private static final String SØKER_QUERY = "query-person.graphql";
    private static final String ANNEN_FORELDER_QUERY = "query-annen-forelder.graphql";

    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);
    private final GraphQLWebClient userClient;
    private final GraphQLWebClient systemClient;

    private final TokenUtil tokenUtil;
    private PDLConfig cfg;

    public PDLConnection(@Qualifier(PDL_USER) GraphQLWebClient userClient, @Qualifier(PDL_SYSTEM) GraphQLWebClient systemClient,
            RestOperations restOperations, PDLConfig cfg, TokenUtil tokenUtil) {
        super(restOperations);
        this.userClient = userClient;
        this.systemClient = systemClient;
        this.tokenUtil = tokenUtil;
        this.cfg = cfg;
    }

    public SøkerDTO hentSøker() {
        var p = oppslagSøker(tokenUtil.getSubject());
        LOG.info("PDL-søker {} har {} relasjon(er) {}", tokenUtil.getSubject(), p.getFamilierelasjoner().size(), p.getFamilierelasjoner());
        var barn = barn(p);
        LOG.info("PDL-søker {} har {} barn {}", tokenUtil.getSubject(), barn.size(), barn);
        var m = PDLMapper.map(tokenUtil.getSubject(), målform(), kontonr(), barn, p);
        LOG.info("PDL-søker mappet til {}", m);
        return m;
    }

    private Set<PDLBarn> barn(PDLSøker p) {
        return p.getFamilierelasjoner()
                .stream()
                .filter(b -> b.getRelatertPersonrolle().equals(BARN))
                .filter(Objects::nonNull)
                .map(b -> oppslagBarn(p.getId(), b.getId()))
                .collect(toSet());
    }

    private PDLBarn oppslagBarn(String fnrSøker, String id) {
        LOG.info("PDL barn oppslag med id {} for søker {}", id, fnrSøker);
        var r = systemClient.post(BARN_QUERY, idFra(id), PDLBarn.class).block();
        LOG.info("PDL oppslag av barn er {}", r);
        String annenPartId = r.annenPart(fnrSøker);
        LOG.info("Annen part er {}", annenPartId);
        var annenPart = oppslagAnnenForelder(annenPartId);
        return r.withId(id).withAnnenForelder(annenPart);
    }

    private PDLSøker oppslagSøker(String id) {
        LOG.info("PDL person oppslag med id {}", id);
        var p = userClient.post(SØKER_QUERY, idFra(id), PDLSøker.class).block();
        LOG.info("PDL oppslag av person er {}", p);
        return p.withId(id);
    }

    private PDLAnnenForelder oppslagAnnenForelder(String id) {
        LOG.info("PDL annen forelder oppslag med id {}", id);
        var a = systemClient.post(ANNEN_FORELDER_QUERY, idFra(id), PDLAnnenForelder.class).block();
        LOG.info("PDL oppslag av annen forelder er {}", a);
        return a.withId(id);
    }

    public Navn oppslagNavn(String id) {
        var n = userClient.post("query-navn.graphql", Map.of("ident", id), PDLNavn.class).block();
        LOG.info("PDL navn for {} er {}", id, n);
        return new Navn(n.getFornavn(), n.getMellomnavn(), n.getEtternavn(), null);
    }

    private static Map<String, Object> idFra(String id) {
        return Map.of("ident", id);
    }

    private Bankkonto kontonr() {
        return getForObject(cfg.getKontonummerURI(), Bankkonto.class);
    }

    private String målform() {
        return getForObject(cfg.getMaalformURI(), String.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [userClient=" + userClient + ", systemClient=" + systemClient + ", tokenUtil=" + tokenUtil + ", cfg="
                + cfg + "]";
    }

    @Override
    public String ping() {
        options(pingEndpoint());
        return "OK";
    }

    @Override
    public URI pingEndpoint() {
        return cfg.pingEndpoint();
    }

    @Override
    public String name() {
        return cfg.name();
    }
}
