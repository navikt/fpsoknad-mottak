package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

public class EndringsSøknad extends Søknad {

    private final String saksnr;

    public EndringsSøknad(LocalDateTime mottattDato, Søker søker, Fordeling fordeling, String saksnr,
            List<Vedlegg> vedlegg) {
        super(mottattDato, søker, new Foreldrepenger(null, null, null, null, null, fordeling, null), vedlegg);
        this.saksnr = saksnr;
    }

    public String getSaksnr() {
        return saksnr;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnr=" + saksnr + ", fordeling="
                + Foreldrepenger.class.cast(getYtelse()).getFordeling() + "]";
    }

}
