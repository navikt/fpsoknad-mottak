package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.List;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
public class Ettersending {

    private final String saksnr;

    private final List<Vedlegg> vedlegg;

    public Ettersending(String saksnr, List<Vedlegg> vedlegg) {
        this.saksnr = saksnr;
        this.vedlegg = vedlegg;
    }

    public String getSaksnr() {
        return saksnr;
    }

    public List<Vedlegg> getVedlegg() {
        return vedlegg;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [saksnr=" + saksnr + ", vedlegg=" + vedlegg + "]";
    }
}
