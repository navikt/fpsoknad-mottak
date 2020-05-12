package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import org.springframework.stereotype.Service;

@Service
public class PdfGeneratorTjeneste {
    private final PdfGeneratorConnection connection;

    public PdfGeneratorTjeneste(PdfGeneratorConnection connection) {
        this.connection = connection;
    }

    public byte[] generate(DokumentBestilling dokument) {
        return connection.genererPdf(dokument);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection +"]";
    }
}
