package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLFamilierelasjon;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLFødselsdato;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLKjønn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLNavn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLPerson.PDLStatsborgerskap;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.BarnDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.PersonDTO;

class PDLMapper {

    private static final Logger LOG = LoggerFactory.getLogger(PDLMapper.class);

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
                .barn(barnFra(p.getFamilierelasjoner()))
                .build();
    }

    private static Set<BarnDTO> barnFra(Set<PDLFamilierelasjon> familierelasjoner) {
        if (CollectionUtils.isEmpty(familierelasjoner)) {
            LOG.info("Ingen familierelasjoner");
            return Set.of();
        }
        LOG.info("Mapper {} relasjoner ({})", familierelasjoner.size(), familierelasjoner);
        return Set.of(); // TODO
    }

    private static CountryCode landkodeFra(Set<PDLStatsborgerskap> statsborgerskap) {
        return landkodeFra(onlyElem(statsborgerskap));
    }

    private static CountryCode landkodeFra(PDLStatsborgerskap statsborgerskap) {
        return statsborgerskap.getLand();
    }

    private static LocalDate fødselsdatoFra(Set<PDLFødselsdato> datoer) {
        return fødselsdatoFra(onlyElem(datoer));
    }

    private static LocalDate fødselsdatoFra(PDLFødselsdato dato) {
        return dato.getFødselsdato();
    }

    private static Navn navnFra(List<PDLNavn> navn, List<PDLKjønn> kjønn) {
        return navnFra(onlyElem(navn), onlyElem(kjønn));
    }

    private static Navn navnFra(PDLNavn n, PDLKjønn k) {
        return new Navn(n.getFornavn(), n.getMellomnavn(), n.getEtternavn(), kjønnFra(k));

    }

    private static Kjønn kjønnFra(List<PDLKjønn> kjønn) {
        return kjønnFra(onlyElem(kjønn));
    }

    private static Kjønn kjønnFra(PDLKjønn kjønn) {
        return switch (kjønn.getKjønn()) {
            case KVINNE -> K;
            case MANN -> M;
            default -> null;
        };
    }
}
