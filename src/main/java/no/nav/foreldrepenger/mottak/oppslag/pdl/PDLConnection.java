package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_SYSTEM;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_USER;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle.BARN;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import graphql.kickstart.spring.webclient.boot.GraphQLErrorsException;
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
    private final PDLErrorResponseHandler errorHandler;

    PDLConnection(@Qualifier(PDL_USER) GraphQLWebClient userClient, @Qualifier(PDL_SYSTEM) GraphQLWebClient systemClient,
            RestOperations restOperations, PDLConfig cfg, TokenUtil tokenUtil, PDLErrorResponseHandler errorHandler) {
        super(restOperations, cfg);
        this.userClient = userClient;
        this.systemClient = systemClient;
        this.tokenUtil = tokenUtil;
        this.cfg = cfg;
        this.errorHandler = errorHandler;
    }

    SøkerDTO hentSøker() {
        return Optional.ofNullable(oppslagSøker(tokenUtil.getSubject()))
                .map(s -> PDLMapper.map(tokenUtil.getSubject(), målform(), kontonr(), barn(s), s))
                .orElse(null);
    }

    private Set<PDLBarn> barn(PDLSøker søker) {
        return safeStream(søker.getFamilierelasjoner())
                .filter(b -> b.getRelatertPersonrolle().equals(BARN))
                .filter(Objects::nonNull)
                .map(b -> oppslagBarn(søker.getId(), b.getId()))
                .filter(Objects::nonNull)
                .filter(b -> b.erNyligFødt(cfg.getBarnFødtInnen()))
                .filter(not(PDLBarn::erBeskyttet))
                .filter(not(b -> b.erNyligDød(cfg.getDødSjekk())))
                .collect(toSet());
    }

    private PDLSøker oppslagSøker(String id) {
        return Optional.ofNullable(oppslag(() -> userClient.post(cfg.søkerQuery(), idFra(id), PDLSøker.class).block(), "søker"))
                .map(s -> s.withId(id))
                .orElse(null);
    }

    private PDLBarn oppslagBarn(String fnrSøker, String id) {
        return Optional.ofNullable(oppslag(() -> systemClient.post(cfg.barnQuery(), idFra(id), PDLBarn.class).block(), "barn"))
                .map(b -> medAnnenPart(b, fnrSøker))
                .map(b -> b.withId(id))
                .orElse(null);
    }

    private PDLBarn medAnnenPart(PDLBarn barn, String fnrSøker) {
        return Optional.ofNullable(barn.annenPart(fnrSøker))
                .map(id -> barn.withAnnenPart(oppslagAnnenPart(id)))
                .orElse(barn);
    }

    private PDLAnnenPart oppslagAnnenPart(String id) {
        return Optional.ofNullable(oppslag(() -> systemClient.post(cfg.annenQuery(), idFra(id), PDLAnnenPart.class).block(), "annen part"))
                .filter(not(PDLAnnenPart::erDød))
                .map(a -> a.withId(id))
                .orElse(null);
    }

    private <T> T oppslag(Supplier<T> oppslag, String type) {
        try {
            LOG.info("PDL oppslag {}", type);
            var res = oppslag.get();
            LOG.info("PDL oppslag {} respons {}", type, res);
            return res;
        } catch (GraphQLErrorsException e) {
            return errorHandler.handle(e);
        }
    }

    public Navn oppslagNavn(String id) {
        var n = oppslag(() -> userClient.post(cfg.navnQuery(), idFra(id), PDLNavn.class).block(), "navn");
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
