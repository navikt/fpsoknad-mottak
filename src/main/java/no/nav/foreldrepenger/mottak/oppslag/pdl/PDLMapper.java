package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static java.util.stream.Collectors.toSet;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.onlyElem;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
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

    static SøkerDTO map(String fnrSøker, String målform, Bankkonto bankkonto, Set<PDLBarn> barn, PDLSøker søker) {
        return SøkerDTO.builder()
                .id(fnrSøker)
                .landKode(landkodeFra(søker.getStatsborgerskap()))
                .fødselsdato(fødselsdatoFra(søker.getFødselsdato()))
                .navn(navnFra(søker.getNavn(), søker.getKjønn()))
                .bankkonto(bankkonto)
                .målform(målform)
                .kjønn(kjønnFra(søker.getKjønn()))
                .barn(barnFra(fnrSøker, barn))
                .build();
    }

    static Navn navnFra(Set<PDLNavn> navn, Set<PDLKjønn> kjønn) {
        return navnFra(onlyElem(navn), onlyElem(kjønn));
    }

    private static Kjønn kjønnFra(Set<PDLKjønn> kjønn) {
        return kjønnFra(onlyElem(kjønn));
    }

    private static Set<BarnDTO> barnFra(String fnrSøker, Set<PDLBarn> barn) {
        return safeStream(barn)
                .map(b -> barnFra(fnrSøker, b))
                .filter(Objects::nonNull)
                .collect(toSet());
    }

    static BarnDTO barnFra(String fnrSøker, PDLBarn barn) {
        return Optional.ofNullable(barn).map(b -> BarnDTO.builder()
                .fnr(Fødselsnummer.valueOf(b.getId()))
                .fnrSøker(Fødselsnummer.valueOf(fnrSøker))
                .navn(navnFra(b.getNavn(), b.getKjønn()))
                .fødselsdato(fødselsdatoFra(b.getFødselsdato()))
                .annenPart(annenPartFra(b.getAnnenPart()))
                .build())
                .orElse(null);
    }

    static AnnenPart annenPartFra(PDLAnnenPart annen) {
        return Optional.ofNullable(annen)
                .map(a -> new AnnenPart(Fødselsnummer.valueOf(annen.getId()), null, navnFra(annen.getNavn(), annen.getKjønn()),
                        fødselsdatoFra(annen.getFødselsdato())))
                .orElse(null);
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

    private static Navn navnFra(PDLNavn n, PDLKjønn k) {
        return new Navn(n.getFornavn(), n.getMellomnavn(), n.getEtternavn(), kjønnFra(k));
    }

    private static Kjønn kjønnFra(PDLKjønn kjønn) {
        return switch (kjønn.getKjønn()) {
            case KVINNE -> K;
            case MANN -> M;
            default -> {
                LOG.warn("Fikk ikke kjønn, antar mann");
                yield M;
            }
        };
    }
}
