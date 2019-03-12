package no.nav.foreldrepenger.mottak.innsending.mappers;

import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.mottak.domain.felles.SpråkKode.defaultSpråk;

import java.util.Optional;

import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.SpråkKode;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.Periode;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Innsendingstype;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Spraakkode;

final class V3DomainMapperUtils {

    private V3DomainMapperUtils() {

    }

    static Innsendingstype innsendingstypeFra(InnsendingsType innsendingsType) {

        switch (innsendingsType) {
        case SEND_SENERE:
            return innsendingsTypeMedKodeverk(SEND_SENERE);
        case LASTET_OPP:
            return innsendingsTypeMedKodeverk(LASTET_OPP);
        default:
            throw new UnexpectedInputException("Innsendingstype " + innsendingsType + "  ikke støttet");
        }
    }

    static Innsendingstype innsendingsTypeMedKodeverk(InnsendingsType type) {
        Innsendingstype typeMedKodeverk = new Innsendingstype().withKode(type.name());
        return typeMedKodeverk.withKodeverk(typeMedKodeverk.getKodeverk());
    }

    static Spraakkode språkFra(Søker søker) {
        return Optional.ofNullable(søker)
                .map(Søker::getSpråkkode)
                .map(SpråkKode::name)
                .map(V3DomainMapperUtils::språkKodeFra)
                .orElse(defaultSpråkKode());
    }

    private static Spraakkode defaultSpråkKode() {
        return språkKodeFra(defaultSpråk());
    }

    private static Spraakkode språkKodeFra(SpråkKode kode) {
        return språkKodeFra(kode.name());
    }

    private static Spraakkode språkKodeFra(String kode) {
        return new Spraakkode().withKode(kode);
    }

    static Periode periodeFra(ÅpenPeriode periode) {
        return Optional.ofNullable(periode)
                .map(p -> new Periode()
                        .withFom(p.getFom())
                        .withTom(p.getTom()))
                .orElse(null);
    }

}
