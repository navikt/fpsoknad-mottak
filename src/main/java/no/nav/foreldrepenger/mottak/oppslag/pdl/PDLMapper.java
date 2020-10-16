package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenPart;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.BarnDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;

class PDLMapper {

    private static final Logger LOG = LoggerFactory.getLogger(PDLMapper.class);

    private PDLMapper() {

    }

    static SøkerDTO map(String fnrSøker, String målform, Bankkonto bankkonto, Set<PDLBarn> barn, PDLSøker p) {
        LOG.info("Mapper søker {} {} {} {} {}", fnrSøker, målform, bankkonto, barn, p);
        var s = SøkerDTO.builder()
                .id(fnrSøker)
                .landKode(landkodeFra(p.getStatsborgerskap()))
                .fødselsdato(fødselsdatoFra(p.getFødselsdato()))
                .navn(navnFra(p.getNavn(), p.getKjønn()))
                .bankkonto(bankkonto)
                .målform(målform)
                .kjønn(kjønnFra(p.getKjønn()))
                .barn(barnFra(fnrSøker, barn))
                .build();
        LOG.info("Mappet søker til {}", s);
        return s;
    }

    private static Kjønn kjønnFra(Set<PDLKjønn> kjønn) {
        return kjønnFra(onlyElem(kjønn));
    }

    private static Set<BarnDTO> barnFra(String fnrSøker, Set<PDLBarn> barn) {
        return safeStream(barn)
                .map(b -> barnFra(fnrSøker, b))
                .collect(toSet());
    }

    static BarnDTO barnFra(String fnrSøker, PDLBarn barn) {
        LOG.info("Mapper barn {} {}", barn.getId(), barn);
        var b = BarnDTO.builder()
                .fnr(Fødselsnummer.valueOf(barn.getId()))
                .fnrSøker(Fødselsnummer.valueOf(fnrSøker))
                .fødselsdato(fødselsdatoFra(barn.getFødselsdato()))
                .annenPart(annenPartFra(barn.getAnnenPart()))
                .build();
        LOG.info("Mappet barn til {}", barn);
        return b;
    }

    static AnnenPart annenPartFra(PDLAnnenPart annen) {
        LOG.info("Mapper annen part {}", annen);
        var an = new AnnenPart(Fødselsnummer.valueOf(annen.getId()), null, navnFra(annen.getNavn(), annen.getKjønn()));
        LOG.info("Mappet annen part til {}", an);
        return an;
    }

    private static CountryCode landkodeFra(Set<PDLStatsborgerskap> statsborgerskap) {
        return landkodeFra(onlyElem(statsborgerskap));
    }

    private static CountryCode landkodeFra(PDLStatsborgerskap statsborgerskap) {
        return CountryCode.getByAlpha3Code(statsborgerskap.getLand());
    }

    private static LocalDate fødselsdatoFra(Set<PDLFødsel> datoer) {
        return fødselsdatoFra(onlyElem(datoer));
    }

    private static LocalDate fødselsdatoFra(PDLFødsel dato) {
        return dato.getFødselsdato();
    }

    static Navn navnFra(Set<PDLNavn> navn, Set<PDLKjønn> kjønn) {
        return navnFra(onlyElem(navn), onlyElem(kjønn));
    }

    static Navn navnFra(PDLNavn n, PDLKjønn k) {
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
