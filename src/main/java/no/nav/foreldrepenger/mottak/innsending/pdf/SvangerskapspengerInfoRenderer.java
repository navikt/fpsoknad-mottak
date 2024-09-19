package no.nav.foreldrepenger.mottak.innsending.pdf;

import java.util.List;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.svangerskapspenger.AvtaltFerie;

@Component
public class SvangerskapspengerInfoRenderer extends FellesSøknadInfoRenderer {

    public SvangerskapspengerInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        super(renderer, textFormatter);
    }

    protected float feriePerioder(List<AvtaltFerie> avtaltFerie, FontAwareCos cos, float y) {
        // placeholder for avtaltFerie
        return y;
    }

}
