package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;


import org.springframework.stereotype.Service;

import no.nav.boot.conditionals.ConditionalOnK8s;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

@Service
@ConditionalOnK8s
public class PdfGeneratorTjeneste implements PdfGenerator {
    private final PdfGeneratorConnection connection;

    public PdfGeneratorTjeneste(PdfGeneratorConnection connection) {
        this.connection = connection;
    }

    @Override
    public byte[] generate(DokumentBestilling dokument) {
        return connection.genererPdf(dokument);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection +"]";
    }
}
