package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;

import java.time.LocalDate;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLFødselsdato;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLKjønn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLNavn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLStatsborgerskap;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.PersonDTO;

class PDLMapper {
    private PDLMapper() {

    }

    static PersonDTO map(String id, String målform, Bankkonto bankkonto, PDLPerson p) {
        return PersonDTO.builder()
                .id(id)
                .landKode(landkodeFra(p.getStatsborgerskap()))
                .fødselsdato(fødselsdatoFra(p.getFødselsdato()))
                .navn(navnFra(p.getNavn(), p.getKjønn()))
                .bankkonto(bankkonto)
                .målform(målform)
                .barn(List.of()) // TODO
                .build();
    }

    private static CountryCode landkodeFra(List<PDLStatsborgerskap> statsborgerskap) {
        return onlyElem(statsborgerskap).getLand();
    }

    private static LocalDate fødselsdatoFra(List<PDLFødselsdato> datoer) {
        return onlyElem(datoer).getFødselsdato();
    }

    private static Navn navnFra(List<PDLNavn> navn, List<PDLKjønn> kjønn) {
        var n = onlyElem(navn);
        return new Navn(n.getFornavn(), n.getMellomnavn(), n.getEtternavn(), kjønnFra(kjønn));
    }

    private static Kjønn kjønnFra(List<PDLKjønn> kjønn) {
        return switch (onlyElem(kjønn).getKjønn()) {
            case KVINNE -> K;
            case MANN -> M;
            default -> null;
        };
    }
}
