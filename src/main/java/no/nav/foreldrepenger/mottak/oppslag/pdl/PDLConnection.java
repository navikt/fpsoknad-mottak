package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.function.Predicate.not;
import static no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import no.nav.foreldrepenger.mottak.http.WebClientRetryAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.Konto;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.UtenlandskKontoInfo;

@Component
public class PDLConnection implements PingEndpointAware {

    private static final String IDENT = "ident";
    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);

    private final GraphQLWebClient userClient;
    private final GraphQLWebClient systemClient;
    private final PDLConfig cfg;
    private final DigdirKrrProxyConnection digdir;
    private final PDLErrorResponseHandler errorHandler;
    private final KontoregisterConnection kontoregister;
    private final TokenUtil tokenUtil;

    PDLConnection(@Qualifier(PDL_USER) GraphQLWebClient userClient,
                  @Qualifier(PDL_SYSTEM) GraphQLWebClient systemClient,
                  PDLConfig cfg, DigdirKrrProxyConnection digdir,
                  KontoregisterConnection kontoregister,
                  TokenUtil tokenUtil,
                  PDLErrorResponseHandler errorHandler) {
        this.userClient = userClient;
        this.systemClient = systemClient;
        this.digdir = digdir;
        this.kontoregister = kontoregister;
        this.cfg = cfg;
        this.tokenUtil = tokenUtil;
        this.errorHandler = errorHandler;
    }

    public Person hentSøker() {
        return hentSøkerInternal(b -> b.erNyligFødt(cfg.getBarnFødtInnen()));
    }

    public Person hentSøkerMedAlleBarn() {
        return hentSøkerInternal(b -> true);
    }

    private Person hentSøkerInternal(Predicate<PDLBarn> filter) {
        var fnrSøker = tokenUtil.autentisertBrukerOrElseThrowException();
        return Optional.ofNullable(oppslagSøker(fnrSøker))
            .map(s -> map(fnrSøker, aktøridFor(fnrSøker), målform(), kontonr(), barn(s, filter), s))
            .orElse(null);
    }

    public Navn navnFor(String id) {
        return Optional.ofNullable(oppslag(() -> postClientCredential(NAVN_QUERY, id, PDLWrappedNavn.class), "navn"))
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

    private List<PDLBarn> barn(PDLSøker søker, Predicate<PDLBarn> filter) {
        var barn = safeStream(søker.getForelderBarnRelasjon()).filter(
                b -> b.relatertPersonsrolle().equals(BARN))
            .map(PDLForelderBarnRelasjon::id)
            .filter(Objects::nonNull)
            .map(b -> oppslagBarn(søker.getId(), b))
            .filter(Objects::nonNull)
            .distinct();
        var dødFødtBarn = safeStream(søker.getDødfødtBarn())
            .filter(dfb -> dfb.dato() != null)
            .map(PDLConnection::mapDødfødte);
        return Stream.concat(barn, dødFødtBarn)
                .filter(filter)
                .toList();
    }

    private static PDLBarn mapDødfødte(PDLDødfødtBarn dfb) {
        return new PDLBarn(Set.of(new PDLFødsel(dfb.dato())), Set.of(), Set.of(), Set.of(), Set.of(),
            Set.of(new PDLDødsfall(dfb.dato())));
    }

    private PDLSøker oppslagSøker(Fødselsnummer fnr) {
        return Optional.ofNullable(oppslag(() -> postOnBehalfOf(SØKER_QUERY, fnr.value(), PDLSøker.class), "søker"))
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
        return oppslag(() -> postClientCredential(IDENT_QUERY, id, PDLIdenter.class), type);
    }

    private PDLBarn oppslagBarn(String fnrSøker, String id) {
        return Optional.ofNullable(oppslag(() -> postClientCredential(BARN_QUERY, id, PDLBarn.class), "barn"))
            .filter(b -> !b.erBeskyttet())
            .map(b -> medAnnenPart(b, fnrSøker))
            .map(b -> b.withId(id))
            .orElse(null);
    }

    private PDLBarn medAnnenPart(PDLBarn barn, String fnrSøker) {
        return Optional.ofNullable(barn.annenPart(fnrSøker))
                .map(id -> barn.withAnnenPart(oppslagAnnenPart(id)))
                .orElse(barn);
    }

    public Optional<Navn> annenPart(String id) {
        var pdlAnnenPart = oppslagAnnenPart(id);
        return Optional.ofNullable(pdlAnnenPart).map(ap -> PDLMapper.navnFra(ap.getNavn()));
    }

    private PDLAnnenPart oppslagAnnenPart(String id) {
        return Optional.ofNullable(oppslag(() -> postClientCredential(ANNEN_PART_QUERY, id, PDLAnnenPart.class), "annenpart"))
                .filter(not(PDLAnnenPart::erDød))
                .filter(not(PDLAnnenPart::erBeskyttet))
                .map(a -> a.withId(id))
                .orElse(null);
    }

    private <T> T postOnBehalfOf(String query, String id, Class<T> responseType) {
        return post(userClient, query, id, responseType);
    }

    private <T> T postClientCredential(String query, String id, Class<T> responseType) {
        return post(systemClient, query, id, responseType);
    }

    @WebClientRetryAware
    private <T> T post(GraphQLWebClient client, String query, String id, Class<T> responseType) {
        return client.post(query, idFra(id), responseType)
            .onErrorMap(this::mapTilKjentGraphQLException)
//            .retryWhen(retryOnlyOn5xxFailures(cfg.getBaseUri().toString()))
            .block();
    }

    private Throwable mapTilKjentGraphQLException(Throwable throwable) {
        if (throwable instanceof GraphQLErrorsException graphQLErrorsException) {
            return errorHandler.handleError(graphQLErrorsException);
        } else {
            return throwable;
        }
    }

    private <T> T oppslag(Supplier<T> oppslag, String type) {
        LOG.info("PDL oppslag {}", type);
        var res = oppslag.get();
        LOG.trace(CONFIDENTIAL, "PDL oppslag {} respons={}", type, res);
        LOG.info("PDL oppslag {} OK", type);
        return res;
    }

    private static Map<String, Object> idFra(String id) {
        return Map.of(IDENT, id);
    }

    Bankkonto kontonr() {
        return tilBankkonto(kontoregister.kontonummer());
    }

    private static Bankkonto tilBankkonto(Konto kontoinformasjon) {
        if (kontoinformasjon != null && !Konto.UKJENT.equals(kontoinformasjon)) {
            var kontonummer = kontoinformasjon.kontonummer();
            var banknavn = Optional.ofNullable(kontoinformasjon.utenlandskKontoInfo())
                .map(UtenlandskKontoInfo::banknavn)
                .orElse(null);
            return new Bankkonto(kontonummer, banknavn);
        }
        return Bankkonto.UKJENT;
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
    public String toString() {
        return getClass().getSimpleName() + " [userClient=" + userClient + ", systemClient=" + systemClient + ", cfg=" + cfg + "]";
    }
}
