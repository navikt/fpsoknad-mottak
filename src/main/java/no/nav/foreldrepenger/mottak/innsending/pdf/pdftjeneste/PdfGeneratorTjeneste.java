package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEFAULT;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;

@Service
@Profile({DEV, DEFAULT})
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
