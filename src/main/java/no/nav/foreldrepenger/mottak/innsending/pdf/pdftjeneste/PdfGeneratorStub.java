package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;


import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnLocal;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

@Service
@ConditionalOnLocal
public class PdfGeneratorStub implements PdfGenerator {

    @Override
    public byte[] generate(DokumentBestilling dokument) {
        return new byte[0];
    }
}
