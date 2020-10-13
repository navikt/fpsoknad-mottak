package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.AnnenForelderDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.BarnDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.PersonDTO;

class PDLMapper {

    private static final Logger LOG = LoggerFactory.getLogger(PDLMapper.class);

    private PDLMapper() {

    }

    static PersonDTO map(String fnrSøker, String målform, Bankkonto bankkonto, Set<PDLBarn> barn, PDLPerson p) {
        return PersonDTO.builder()
                .id(fnrSøker)
                .landKode(landkodeFra(p.getStatsborgerskap()))
                .fødselsdato(fødselsdatoFra(p.getFødselsdato()))
                .navn(navnFra(p.getNavn(), p.getKjønn()))
                .bankkonto(bankkonto)
                .målform(målform)
                .barn(barnFra(fnrSøker, barn))
                .build();
    }

    private static Set<BarnDTO> barnFra(String fnrSøker, Set<PDLBarn> barn) {
        return safeStream(barn)
                .map(b -> barnFra(fnrSøker, b))
                .collect(toSet());
    }

    private static BarnDTO barnFra(String fnrSøker, PDLBarn barn) {
        LOG.info("Mapper barn {} {}", barn.getId(), barn);
        return BarnDTO.builder()
                .fnr(Fødselsnummer.valueOf(barn.getId()))
                .fnrSøker(Fødselsnummer.valueOf(fnrSøker))
                .fødselsdato(fødselsdatoFra(barn.getFødselsdato()))
                .annenForelder(annenForelderFra(barn.getAnnenForelder()))
                .build();
    }

    private static AnnenForelderDTO annenForelderFra(PDLAnnenForelder annen) {
        LOG.info("Mapper annen foreldrer {}", annen);
        var an = Optional.ofNullable(annen)
                .map(a -> AnnenForelderDTO.builder()
                        .fnr(annen.getId())
                        .fødselsdato(fødselsdatoFra(annen.getFødselsdato()))
                        .navn(navnFra(annen.getNavn(), annen.getKjønn()))
                        .build())
                .orElse(null);
        LOG.info("Mappet annen foreldrer til {}", an);
        return an;
    }

    private static CountryCode landkodeFra(Set<PDLStatsborgerskap> statsborgerskap) {
        return landkodeFra(onlyElem(statsborgerskap));
    }

    private static CountryCode landkodeFra(PDLStatsborgerskap statsborgerskap) {
        return statsborgerskap.getLand();
    }

    private static LocalDate fødselsdatoFra(Set<PDLFødsel> datoer) {
        return fødselsdatoFra(onlyElem(datoer));
    }

    private static LocalDate fødselsdatoFra(PDLFødsel dato) {
        return dato.getFødselsdato();
    }

    private static Navn navnFra(Set<PDLNavn> navn, Set<PDLKjønn> kjønn) {
        return navnFra(onlyElem(navn), onlyElem(kjønn));
    }

    private static Navn navnFra(PDLNavn n, PDLKjønn k) {
        return new Navn(n.getFornavn(), n.getMellomnavn(), n.getEtternavn(), kjønnFra(k));

    }

    private static Kjønn kjønnFra(PDLKjønn kjønn) {
        return switch (kjønn.getKjønn()) {
            case KVINNE -> K;
            case MANN -> M;
            default -> null;
        };
    }
}
