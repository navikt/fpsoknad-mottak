package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_SYSTEM;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_USER;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle.BARN;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
public class PDLConnection extends AbstractRestConnection implements PingEndpointAware {

    private static final String IDENT = "ident";
    private static final String NAVN_QUERY = "query-navn.graphql";
    private static final String BARN_QUERY = "query-barn.graphql";
    private static final String SØKER_QUERY = "query-person.graphql";
    private static final String ANNEN_PART_QUERY = "query-annen-forelder.graphql";

    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);
    private final GraphQLWebClient userClient;
    private final GraphQLWebClient systemClient;
    @Inject
    private ObjectMapper mapper;
    private final TokenUtil tokenUtil;
    private PDLConfig cfg;

    public PDLConnection(@Qualifier(PDL_USER) GraphQLWebClient userClient, @Qualifier(PDL_SYSTEM) GraphQLWebClient systemClient,
            RestOperations restOperations, PDLConfig cfg, TokenUtil tokenUtil) {
        super(restOperations, cfg);
        this.userClient = userClient;
        this.systemClient = systemClient;
        this.tokenUtil = tokenUtil;
        this.cfg = cfg;
    }

    public SøkerDTO hentSøker() {
        var p = oppslagSøker(tokenUtil.getSubject());
        LOG.info("PDL søker {} har {} relasjon(er) {}", tokenUtil.getSubject(), p.getFamilierelasjoner().size(), p.getFamilierelasjoner());
        var barn = barn(p);
        LOG.info("PDL søker {} har {} barn {}", tokenUtil.getSubject(), barn.size(), barn);
        var m = PDLMapper.map(tokenUtil.getSubject(), målform(), kontonr(), barn, p);
        LOG.info("PDL søker mappet til {}", m);
        try {
            LOG.info("JSON " + mapper.writeValueAsString(m));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return m;
    }

    private Set<PDLBarn> barn(PDLSøker p) {
        return p.getFamilierelasjoner()
                .stream()
                .filter(b -> b.getRelatertPersonrolle().equals(BARN))
                .filter(Objects::nonNull)
                .map(b -> oppslagBarn(p.getId(), b.getId()))
                .filter(PDLConnection::erNyligFØdt)
                .filter(not(PDLConnection::erBeskyttet))
                .filter(not(PDLConnection::erDød))
                .collect(toSet());
    }

    private static boolean erDød(PDLBarn b) {
        return safeStream(b.getDødsfall())
                .map(PDLDødsfall::getDødsdato)
                .anyMatch(d -> d.isAfter(LocalDate.now().minusMonths(4)));
    }

    private static boolean erBeskyttet(PDLBarn b) {
        return !onlyElem(b.getBeskyttelse()).getGradering().equals(PDLAdresseGradering.UGRADERT);
    }

    private static boolean erNyligFØdt(PDLBarn b) {
        return onlyElem(b.getFødselsdato()).getFødselsdato().isAfter(LocalDate.now().minusMonths(24));
    }

    private PDLBarn oppslagBarn(String fnrSøker, String id) {
        LOG.info("PDL barn oppslag med id {} for søker {}", id, fnrSøker);
        var r = systemClient.post(BARN_QUERY, idFra(id), PDLBarn.class).block();
        LOG.info("PDL oppslag av barn er {}", r);
        String annenPartId = r.annenPart(fnrSøker);
        LOG.info("PDL oppslag annen part er {}", annenPartId);
        r.withId(id).withAnnenPart(oppslagAnnenPart(annenPartId));
        LOG.info("PDL barn oppslag er", r);
        return r;

    }

    private PDLSøker oppslagSøker(String id) {
        LOG.info("PDL person oppslag med id {}", id);
        var p = userClient.post(SØKER_QUERY, idFra(id), PDLSøker.class).block();
        LOG.info("PDL oppslag av person med id {} er {}", id, p);
        return p.withId(id);
    }

    private PDLAnnenPart oppslagAnnenPart(String id) {
        LOG.info("PDL annen part oppslag med id {}", id);
        var a = systemClient.post(ANNEN_PART_QUERY, idFra(id), PDLAnnenPart.class).block();
        LOG.info("PDL annen part oppslag med id {} er {}", id, a);
        return a.withId(id);
    }

    public Navn oppslagNavn(String id) {
        LOG.info("PDL navn oppslag med id {}", id);
        var n = userClient.post(NAVN_QUERY, idFra(id), PDLNavn.class).block();
        LOG.info("PDL navn for {} er {}", id, n);
        return new Navn(n.getFornavn(), n.getMellomnavn(), n.getEtternavn(), null);
    }

    private static Map<String, Object> idFra(String id) {
        return Map.of(IDENT, id);
    }

    private Bankkonto kontonr() {
        return getForObject(cfg.getKontonummerURI(), Bankkonto.class);
    }

    private String målform() {
        return getForObject(cfg.getMaalformURI(), String.class);
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [userClient=" + userClient + ", systemClient=" + systemClient + ", tokenUtil=" + tokenUtil + ", cfg="
                + cfg + "]";
    }

}
