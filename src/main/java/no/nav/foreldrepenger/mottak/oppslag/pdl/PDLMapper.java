package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.common.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.common.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.common.domain.felles.Kjønn.U;
import static no.nav.foreldrepenger.common.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLExceptionGeneratingResponseHander.exception;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Barn;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.AnnenPart;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.Sivilstand;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLIdentInformasjon.PDLIdentGruppe;

class PDLMapper {

    private PDLMapper() {
    }

    static Person map(Fødselsnummer fnrSøker, AktørId aktørId, Målform målform, Bankkonto bankkonto, List<PDLBarn> barn, PDLSøker søker) {
        return Person.builder()
            .fnr(fnrSøker)
            .aktørId(aktørId)
            .fødselsdato(fødselsdatoFra(søker.getFødselsdato()))
            .navn(navnFra(søker.getNavn()))
            .kjønn(kjønnFra(søker.getKjønn()))
            .bankkonto(bankkonto)
            .målform(målform)
            .kjønn(kjønnFra(søker.getKjønn()))
            .barn(barnFra(barn))
            .sivilstand(sivilstandFra(søker.getSivilstand()))
            .build();
    }

    private static Sivilstand sivilstandFra(Set<PDLSivilstand> sivilstand) {
        return safeStream(sivilstand).findFirst().map(PDLMapper::map).orElse(null);
    }

    private static Sivilstand map(PDLSivilstand s) {
        return new Sivilstand(map(s.type()));
    }

    private static Sivilstand.Type map(PDLSivilstand.Type type) {
        return switch (type) {
            case UOPPGITT -> Sivilstand.Type.UOPPGITT;
            case UGIFT -> Sivilstand.Type.UGIFT;
            case GIFT -> Sivilstand.Type.GIFT;
            case ENKE_ELLER_ENKEMANN -> Sivilstand.Type.ENKE_ELLER_ENKEMANN;
            case SKILT -> Sivilstand.Type.SKILT;
            case SEPARERT -> Sivilstand.Type.SEPARERT;
            case REGISTRERT_PARTNER -> Sivilstand.Type.REGISTRERT_PARTNER;
            case SEPARERT_PARTNER -> Sivilstand.Type.SEPARERT_PARTNER;
            case SKILT_PARTNER -> Sivilstand.Type.SKILT_PARTNER;
            case GJENLEVENDE_PARTNER -> Sivilstand.Type.GJENLEVENDE_PARTNER;
        };
    }

    static Navn navnFra(Set<PDLNavn> navn) {
        return navnFra(onlyElem(navn));
    }

    private static Kjønn kjønnFra(Set<PDLKjønn> kjønn) {
        return kjønnFra(onlyElem(kjønn));
    }

    private static List<Barn> barnFra(List<PDLBarn> barn) {
        return safeStream(barn)
            .map(PDLMapper::barnFra)
            .filter(Objects::nonNull)
            .sorted(comparing(Barn::fødselsdato))
            .toList();
    }

    static Barn barnFra(PDLBarn barn) {
        return Optional.ofNullable(barn)
            .map(b -> {
                var fnr = b.getId() == null ? null : new Fødselsnummer(b.getId());
                var fødselsdato = fødselsdatoFra(b.getFødselsdato());
                var dødsdato = dødsdatoFra(b.getDødsfall());
                var navn = b.getNavn() == null || b.getNavn().isEmpty() ? null : navnFra(b.getNavn());
                var kjønn = b.getKjønn() == null || b.getKjønn().isEmpty() ? null : kjønnFra(barn.getKjønn());
                var annenPart = b.getAnnenPart() == null ? null : annenPartFra(b.getAnnenPart());
                return new Barn(fnr, fødselsdato, dødsdato, navn, kjønn, annenPart);
            })
            .orElse(null);
    }

    static AnnenPart annenPartFra(PDLAnnenPart annen) {
        return Optional.ofNullable(annen)
            .map(a -> new AnnenPart(new Fødselsnummer(annen.getId()), null, navnFra(annen.getNavn()),
                fødselsdatoFra(annen.getFødselsdato())))
            .orElse(null);
    }

    private static LocalDate dødsdatoFra(Set<PDLDødsfall> datoer) {
        if (datoer == null || datoer.isEmpty()) {
            return null;
        }
        return onlyElem(datoer).dødsdato();
    }

    private static LocalDate fødselsdatoFra(Set<PDLFødsel> datoer) {
        return fødselsdatoFra(onlyElem(datoer));
    }

    private static LocalDate fødselsdatoFra(PDLFødsel dato) {
        return dato.fødselsdato();
    }

    private static Navn navnFra(PDLNavn n) {
        return new Navn(n.fornavn(), n.mellomnavn(), n.etternavn());
    }

    private static Kjønn kjønnFra(PDLKjønn kjønn) {
        return Optional.ofNullable(kjønn)
            .map(PDLKjønn::kjønn)
            .map(PDLMapper::kjønnFra)
            .orElse(U);
    }

    private static Kjønn kjønnFra(PDLKjønn.Kjønn kjønn) {
        return switch (kjønn) {
            case KVINNE -> K;
            case MANN -> M;
            case UKJENT -> U;
        };
    }

    static String mapIdent(PDLIdenter identer, PDLIdentGruppe gruppe) {
        return safeStream(identer.identer())
            .filter(i -> i.gruppe().equals(gruppe))
            .map(PDLIdentInformasjon::ident)
            .findAny()
            .orElseThrow(() -> exception(NOT_FOUND, "Fant ikke id"));
    }
}
