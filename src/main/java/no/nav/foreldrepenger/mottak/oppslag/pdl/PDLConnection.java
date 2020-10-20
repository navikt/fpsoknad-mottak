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

    private static final String IDENT = "ident";

    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);
    private final GraphQLWebClient userClient;
    private final GraphQLWebClient systemClient;
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
        return m;
    }

    private Set<PDLBarn> barn(PDLSøker s) {
        return safeStream(s.getFamilierelasjoner())
                .filter(b -> b.getRelatertPersonrolle().equals(BARN))
                .filter(Objects::nonNull)
                .map(b -> oppslagBarn(s.getId(), b.getId()))
                .filter(this::erNyligFødt)
                .filter(not(PDLBarn::erBeskyttet))
                .filter(not(this::erNyligDød))
                .collect(toSet());
    }

    private boolean erNyligDød(PDLBarn b) {
        return safeStream(b.getDødsfall())
                .map(PDLDødsfall::getDødsdato)
                .anyMatch(d -> d.isAfter(LocalDate.now().minusMonths(cfg.getDødSjekk())));
    }

    private boolean erNyligFødt(PDLBarn b) {
        return onlyElem(b.getFødselsdato()).getFødselsdato().isAfter(LocalDate.now().minusMonths(cfg.getBarnFødtInnen()));
    }

    private PDLBarn oppslagBarn(String fnrSøker, String id) {
        LOG.info("PDL barn oppslag med id {} for søker {}", id, fnrSøker);
        var r = systemClient.post(cfg.barnQuery(), idFra(id), PDLBarn.class).block();
        r.withId(id);
        LOG.info("PDL oppslag av barn er {}", r);
        String annenPartId = r.annenPart(fnrSøker);
        if (annenPartId != null) {
            LOG.info("PDL oppslag barn annen part er {}", annenPartId);
            r.withAnnenPart(oppslagAnnenPart(annenPartId));
        } else {
            LOG.info("Ingen annen part for søker={} barn={}", fnrSøker, id);
        }

        LOG.info("PDL barn oppslag er", r);
        return r;

    }

    private PDLSøker oppslagSøker(String id) {
        LOG.info("PDL person oppslag med id {}", id);
        var p = userClient.post(cfg.søkerQuery(), idFra(id), PDLSøker.class).block();
        LOG.info("PDL oppslag av person med id {} er {}", id, p);
        return p.withId(id);
    }

    private PDLAnnenPart oppslagAnnenPart(String id) {
        LOG.info("PDL annen part oppslag med id {}", id);
        var a = systemClient.post(cfg.annenQuery(), idFra(id), PDLAnnenPart.class).block();
        LOG.info("PDL annen part oppslag med id {} er {}", id, a);
        return a.withId(id);
    }

    public Navn oppslagNavn(String id) {
        LOG.info("PDL navn oppslag med id {}", id);
        var n = userClient.post(cfg.navnQuery(), idFra(id), PDLNavn.class).block();
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
