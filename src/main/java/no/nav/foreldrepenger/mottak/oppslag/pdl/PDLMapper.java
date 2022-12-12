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
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Barn;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.AnnenPart;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLIdentInformasjon.PDLIdentGruppe;

class PDLMapper {

    private PDLMapper() {
    }

    static Person map(Fødselsnummer fnrSøker, AktørId aktørId, Målform målform, Bankkonto bankkonto, Set<PDLBarn> barn, PDLSøker søker) {
        return Person.builder()
            .fnr(fnrSøker)
            .aktørId(aktørId)
            .land(landkodeFra(søker.getStatsborgerskap()))
            .fødselsdato(fødselsdatoFra(søker.getFødselsdato()))
            .navn(navnFra(søker.getNavn()))
            .kjønn(kjønnFra(søker.getKjønn()))
            .bankkonto(bankkonto)
            .målform(målform)
            .kjønn(kjønnFra(søker.getKjønn()))
            .barn(barnFra(barn))
            .build();
    }

    static Navn navnFra(Set<PDLNavn> navn) {
        return navnFra(onlyElem(navn));
    }

    private static Kjønn kjønnFra(Set<PDLKjønn> kjønn) {
        return kjønnFra(onlyElem(kjønn));
    }

    private static Set<Barn> barnFra(Set<PDLBarn> barn) {
        return safeStream(barn)
            .map(PDLMapper::barnFra)
            .filter(Objects::nonNull)
            .sorted(comparing(Barn::fødselsdato))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    static Barn barnFra(PDLBarn barn) {
        return Optional.ofNullable(barn)
            .map(b -> new Barn(new Fødselsnummer(b.getId()), fødselsdatoFra(b.getFødselsdato()), dødsdatoFra(b.getDødsfall()),
                navnFra(b.getNavn()), kjønnFra(barn.getKjønn()),  annenPartFra(b.getAnnenPart())))
            .orElse(null);
    }

    static AnnenPart annenPartFra(PDLAnnenPart annen) {
        return Optional.ofNullable(annen)
            .map(a -> new AnnenPart(new Fødselsnummer(annen.getId()), null, navnFra(annen.getNavn()),
                fødselsdatoFra(annen.getFødselsdato())))
            .orElse(null);
    }

    private static CountryCode landkodeFra(Set<PDLStatsborgerskap> statsborgerskap) {
        return landkodeFra(onlyElem(statsborgerskap));
    }

    private static CountryCode landkodeFra(PDLStatsborgerskap statsborgerskap) {
        return Optional.ofNullable(statsborgerskap)
            .map(PDLStatsborgerskap::land)
            .map(CountryCode::getByAlpha3Code)
            .orElse(CountryCode.NO);
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
