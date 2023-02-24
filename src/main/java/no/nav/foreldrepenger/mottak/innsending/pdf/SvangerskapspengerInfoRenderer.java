package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.springframework.stereotype.Component;

@Component
public class SvangerskapspengerInfoRenderer extends FellesSøknadInfoRenderer {

    public SvangerskapspengerInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        super(renderer, textFormatter);
    }
}
