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
import static no.nav.foreldrepenger.mottak.oppslag.pdl.Ytelse.FORELDREPENGER;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import graphql.kickstart.spring.webclient.boot.GraphQLErrorsException;
import graphql.kickstart.spring.webclient.boot.GraphQLRequest;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.innsending.mappers.AktørIdTilFnrConverter;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.http.Retry;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DigdirKrrProxyConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.Konto;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.UtenlandskKontoInfo;
import reactor.core.publisher.Mono;

@Component
public class PDLConnection implements AktørIdTilFnrConverter {

    private static final String IDENT = "ident";
    private static final String BEHANDLINGSNUMMER = "behandlingsnummer";
    private static final Logger LOG = LoggerFactory.getLogger(PDLConnection.class);

    private final GraphQLWebClient userClient;
    private final GraphQLWebClient systemClient;
    private final PDLConfig cfg;
    private final DigdirKrrProxyConnection digdir;
    private final PDLErrorResponseHandler errorHandler;
    private final KontoregisterConnection kontoregister;
    private final Ytelse defaultYtelse;


    PDLConnection(@Qualifier(PDL_USER) GraphQLWebClient userClient,
                  @Qualifier(PDL_SYSTEM) GraphQLWebClient systemClient,
                  PDLConfig cfg, DigdirKrrProxyConnection digdir,
                  KontoregisterConnection kontoregister,
                  PDLErrorResponseHandler errorHandler) {
        this.userClient = userClient;
        this.systemClient = systemClient;
        this.digdir = digdir;
        this.kontoregister = kontoregister;
        this.cfg = cfg;
        this.errorHandler = errorHandler;
        this.defaultYtelse = FORELDREPENGER;
    }

    public Person hentPerson(Fødselsnummer fnr) {
        return hentPerson(fnr, defaultYtelse);
    }

    public Person hentPerson(Fødselsnummer fnr, Ytelse ytelse) {
        return hentPersonInternal(fnr, ytelse, b -> b.erNyligFødt(cfg.getBarnFødtInnen()));
    }

    //TODO erstatte med et enklere pdl oppslag... Gjøres opp til 4 separate kall..
    private Person hentPersonInternal(Fødselsnummer fnr, Ytelse ytelse, Predicate<PDLBarn> filter) {
        return Optional.ofNullable(oppslagSøker(fnr, ytelse))
            .map(s -> map(fnr, aktørId(fnr), målform(), kontonr(), barn(s, ytelse, filter), s))
            .orElse(null);
    }

    public Navn navnFor(String id) {
        return Optional.ofNullable(oppslag(() -> postClientCredential(NAVN_QUERY, defaultYtelse, id, PDLWrappedNavn.class), "navn"))
                .flatMap(navn -> safeStream(navn.navn())
                        .findFirst()
                        .map(n -> new Navn(n.fornavn(), n.mellomnavn(), n.etternavn())))
                .orElse(null);
    }

    public AktørId aktørId(Fødselsnummer fnr) {
        return aktørId(fnr, defaultYtelse);
    }

    public AktørId aktørId(Fødselsnummer fnr, Ytelse ytelse) {
        return Optional.ofNullable(fnr)
                .map(id -> oppslagId(id, ytelse))
                .map(id -> mapIdent(id, AKTORID))
                .map(AktørId::new)
                .orElse(null);
    }

    public Fødselsnummer fnr(AktørId aktørId) {
        return fnr(aktørId, defaultYtelse);
    }

    public Fødselsnummer fnr(AktørId aktørId, Ytelse ytelse) {
        return Optional.ofNullable(aktørId)
                .map(id -> oppslagId(id, ytelse))
                .map(id -> mapIdent(id, FOLKEREGISTERIDENT))
                .map(Fødselsnummer::new)
                .orElse(null);
    }

    private List<PDLBarn> barn(PDLSøker søker, Ytelse ytelse, Predicate<PDLBarn> filter) {
        var barn = safeStream(søker.getForelderBarnRelasjon()).filter(
                b -> b.relatertPersonsrolle().equals(BARN))
            .map(PDLForelderBarnRelasjon::id)
            .filter(Objects::nonNull)
            .map(b -> oppslagBarn(søker.getId(), ytelse, b))
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

    private PDLSøker oppslagSøker(Fødselsnummer fnr, Ytelse ytelse) {
        return Optional.ofNullable(oppslag(() -> postOnBehalfOf(SØKER_QUERY, ytelse, fnr.value(), PDLSøker.class), "søker"))
                .map(s -> s.withId(fnr.value()))
                .orElse(null);
    }

    private PDLIdenter oppslagId(Fødselsnummer id, Ytelse ytelse) {
        return oppslagId(id.value(), ytelse, "fødselsnummer");
    }

    private PDLIdenter oppslagId(AktørId id, Ytelse ytelse) {
        return oppslagId(id.value(), ytelse, "aktør");
    }

    private PDLIdenter oppslagId(String id, Ytelse ytelse, String type) {
        return oppslag(() -> postClientCredential(IDENT_QUERY, ytelse, id, PDLIdenter.class), type);
    }

    private PDLBarn oppslagBarn(String fnrSøker, Ytelse ytelse, String id) {
        return Optional.ofNullable(oppslag(() -> postClientCredential(BARN_QUERY, ytelse, id, PDLBarn.class), "barn"))
            .filter(b -> !b.erBeskyttet())
            .map(b -> medAnnenPart(b, ytelse, fnrSøker))
            .map(b -> b.withId(id))
            .orElse(null);
    }

    private PDLBarn medAnnenPart(PDLBarn barn, Ytelse ytelse, String fnrSøker) {
        return Optional.ofNullable(barn.annenPart(fnrSøker))
                .map(id -> barn.withAnnenPart(oppslagAnnenPart(id, ytelse)))
                .orElse(barn);
    }

    private PDLAnnenPart oppslagAnnenPart(String id, Ytelse ytelse) {
        return Optional.ofNullable(oppslag(() -> postClientCredential(ANNEN_PART_QUERY, ytelse, id, PDLAnnenPart.class), "annenpart"))
                .filter(not(PDLAnnenPart::erDød))
                .filter(not(PDLAnnenPart::erBeskyttet))
                .map(a -> a.withId(id))
                .orElse(null);
    }

    private <T> T postOnBehalfOf(String query, Ytelse ytelse, String id, Class<T> responseType) {
        return post(userClient, query, id, ytelse, responseType);
    }

    private <T> T postClientCredential(String query, Ytelse ytelse, String id, Class<T> responseType) {
        return post(systemClient, query, id, ytelse, responseType);
    }

    @Retry
    private <T> T post(GraphQLWebClient client, String query, String id, Ytelse ytelse, Class<T> responseType) {
        var request = GraphQLRequest.builder()
            .header(BEHANDLINGSNUMMER, ytelse.getBehandlingsnummer())
            .resource(query)
            .variables(idFra(id))
            .build();
        return client.post(request)
            .flatMap(it -> {
                it.validateNoErrors();
                return Mono.justOrEmpty(it.getFirst(responseType));
            })
            .onErrorMap(this::mapTilKjentGraphQLException)
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
    public AktørId konverter(Fødselsnummer fnr) {
        return aktørId(fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [userClient=" + userClient + ", systemClient=" + systemClient + ", cfg=" + cfg + "]";
    }
}
