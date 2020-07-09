package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEFAULT;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.LOCAL;

@Service
@Profile(LOCAL)
public class PdfGeneratorStub implements PdfGenerator {

    @Override
    public byte[] generate(DokumentBestilling dokument) {
        return new byte[0];
    }
}
