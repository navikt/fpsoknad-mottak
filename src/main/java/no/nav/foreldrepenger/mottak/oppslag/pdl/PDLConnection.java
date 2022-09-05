package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static no.nav.boot.conditionals.EnvUtil.isDevOrLocal;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_SYSTEM;
import static no.nav.foreldrepenger.mottak.http.WebClientConfiguration.PDL_USER;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig.ANNEN_PART_QUERY;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig.BARN_QUERY;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig.IDENT_QUERY;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig.NAVN_QUERY;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConfig.SØKER_QUERY;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.BARN;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLIdentInformasjon.PDLIdentGruppe.AKTORID;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLIdentInformasjon.PDLIdentGruppe.FOLKEREGISTERIDENT;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLMapper.map;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLMapper.mapIdent;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import graphql.kickstart.spring.webclient.boot.GraphQLErrorsException;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DigdirKrrProxyConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontonummerConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.UtenlandskKontoInfo;

@Component
public class PDLConnection implements PingEndpointAware, EnvironmentAware {

    private static final String IDENT = "ident";
    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);

    private final GraphQLWebClient userClient;
    private final GraphQLWebClient systemClient;
    private final PDLConfig cfg;
    private final DigdirKrrProxyConnection digdir;
    private final PDLErrorResponseHandler errorHandler;
    private final KontonummerConnection kontonr;
    private final KontoregisterConnection kontoregister;
    private final TokenUtil tokenUtil;
    private Environment env;

    PDLConnection(@Qualifier(PDL_USER) GraphQLWebClient userClient,
                  @Qualifier(PDL_SYSTEM) GraphQLWebClient systemClient,
                  PDLConfig cfg, DigdirKrrProxyConnection digdir,
                  KontonummerConnection kontonr,
                  KontoregisterConnection kontoregister,
                  TokenUtil tokenUtil,
                  PDLErrorResponseHandler errorHandler) {
        this.userClient = userClient;
        this.systemClient = systemClient;
        this.digdir = digdir;
        this.kontonr = kontonr;
        this.kontoregister = kontoregister;
        this.cfg = cfg;
        this.tokenUtil = tokenUtil;
        this.errorHandler = errorHandler;
    }

    public Person hentSøker() {
        var fnrSøker = tokenUtil.autentisertBrukerOrElseThrowException();
        return Optional.ofNullable(oppslagSøker(fnrSøker))
                .map(s -> map(fnrSøker, aktøridFor(fnrSøker), målform(), kontonr(), barn(s), s))
                .orElse(null);
    }

    public Navn navnFor(String id) {
        return Optional.ofNullable(oppslag(() -> systemClient.post(NAVN_QUERY, idFra(id), PDLWrappedNavn.class).block(), "navn"))
                .flatMap(navn -> safeStream(navn.navn())
                        .findFirst()
                        .map(n -> new Navn(n.fornavn(), n.mellomnavn(), n.etternavn())))
                .orElse(null);
    }

    public AktørId aktøridFor(Fødselsnummer fnr) {
        return Optional.ofNullable(fnr)
                .map(this::oppslagId)
                .map(id -> mapIdent(id, AKTORID))
                .map(AktørId::valueOf)
                .orElse(null);
    }

    public Fødselsnummer fødselsnummerFor(AktørId aktørId) {
        return Optional.ofNullable(aktørId)
                .map(this::oppslagId)
                .map(id -> mapIdent(id, FOLKEREGISTERIDENT))
                .map(Fødselsnummer::new)
                .orElse(null);

    }

    private Set<PDLBarn> barn(PDLSøker søker) {
        return safeStream(søker.getForelderBarnRelasjon())
                .filter(b -> b.relatertPersonsrolle().equals(BARN))
                .map(PDLForelderBarnRelasjon::id)
                .filter(Objects::nonNull)
                .map(b -> oppslagBarn(søker.getId(), b))
                .filter(Objects::nonNull)
                .filter(b -> b.erNyligFødt(cfg.getBarnFødtInnen()))
                .filter(not(PDLBarn::erBeskyttet))
                .filter(not(b -> b.erNyligDød(cfg.getDødSjekk())))
                .collect(toSet());
    }

    private PDLSøker oppslagSøker(Fødselsnummer fnr) {
        return Optional.ofNullable(oppslag(() -> userClient.post(SØKER_QUERY, idFra(fnr.value()), PDLSøker.class).block(), "søker"))
                .map(s -> s.withId(fnr.value()))
                .orElse(null);
    }

    private PDLIdenter oppslagId(Fødselsnummer id) {
        return oppslagId(id.value(), "fødselsnummer");
    }

    private PDLIdenter oppslagId(AktørId id) {
        return oppslagId(id.value(), "aktør");
    }

    private PDLIdenter oppslagId(String id, String type) {
        return oppslag(() -> systemClient.post(IDENT_QUERY, idFra(id), PDLIdenter.class).block(), type);
    }

    private PDLBarn oppslagBarn(String fnrSøker, String id) {
        return Optional.ofNullable(oppslag(() -> systemClient.post(BARN_QUERY, idFra(id), PDLBarn.class).block(), "barn"))
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
        return Optional
                .ofNullable(oppslag(() -> systemClient.post(ANNEN_PART_QUERY, idFra(id), PDLAnnenPart.class).block(), "annen part"))
                .filter(not(PDLAnnenPart::erDød))
                .filter(not(PDLAnnenPart::erBeskyttet))
                .map(a -> a.withId(id))
                .orElse(null);
    }

    private <T> T oppslag(Supplier<T> oppslag, String type) {
        try {
            LOG.info("PDL oppslag {}", type);
            var res = oppslag.get();
            LOG.trace("PDL oppslag {} respons={}", type, res);
            LOG.info("PDL oppslag {} OK", type);
            return res;
        } catch (GraphQLErrorsException e) {
            LOG.warn("PDL oppslag {} feilet", type, e);
            return errorHandler.handleError(e);
        } catch (Exception e) {
            LOG.warn("PDL oppslag {} feilet med uventet feil", type, e);
            throw e;
        }
    }

    private static Map<String, Object> idFra(String id) {
        return Map.of(IDENT, id);
    }

    private Bankkonto kontonrFraFpsoknadOppslag() {
        try {
            return kontonr.kontonr();
        } catch (Exception e) {
            LOG.warn("Kontonummer oppslag feilet", e);
            return Bankkonto.UKJENT;
        }
    }

    Bankkonto kontonr() {
        final var bankkonto = kontonrFraFpsoknadOppslag();

        if (isDevOrLocal(env)) {
            var bankkontoFraNyttEndepunkt = hentBankkontoFraNyTjenesteFailSafe();
            if (bankkonto != null && !bankkonto.equals(bankkontoFraNyttEndepunkt)) {
                // toString() til Bankkonto sensurer kontonummer
                LOG.warn("Fant avvvik mellom oppslag av kontonummer fra nytt og gammel tjeneste. " +
                        "Fra oppsalg: kontonummer '{}' banknavn '{}' og fra " +
                        "nytt endepunkt kontonummer '{}', banknavn '{}'",
                    bankkonto.kontonummer(), bankkonto.banknavn(),
                    Optional.ofNullable(bankkontoFraNyttEndepunkt).map(Bankkonto::kontonummer).orElse(""),
                    Optional.ofNullable(bankkontoFraNyttEndepunkt).map(Bankkonto::banknavn).orElse(""));
            }
        }
        return bankkonto;
    }

    private Bankkonto hentBankkontoFraNyTjenesteFailSafe() {
        try {
            var kontoinformasjon = kontoregister.kontonrFraNyTjeneste();
            if (kontoinformasjon != null) {
                var kontonummer = kontoinformasjon.kontonummer();
                var banknavn = Optional.ofNullable(kontoinformasjon.utenlandskKontoInfo())
                    .map(UtenlandskKontoInfo::banknavn)
                    .orElse(null);
                return new Bankkonto(kontonummer, banknavn);
            }
            return Bankkonto.UKJENT;
        } catch (Exception e) {
            LOG.warn("Oppslag av kontonummer på nytt endepunkt i dev feilet", e);
            return null;
        }
    }


    private Målform målform() {
        return digdir.målform();
    }

    @Override
    public String ping() {
        // options(pingEndpoint()); TODO finn ut av dette
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
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [userClient=" + userClient + ", systemClient=" + systemClient + ", cfg=" + cfg + "]";
    }

}
