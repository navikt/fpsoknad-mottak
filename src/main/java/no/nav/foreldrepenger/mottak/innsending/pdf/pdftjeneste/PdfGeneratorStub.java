package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.LOCAL;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

@Service
@Profile(LOCAL)
public class PdfGeneratorStub implements PdfGenerator {

    @Override
    public byte[] generate(DokumentBestilling dokument) {
        return new byte[0];
    }
}
