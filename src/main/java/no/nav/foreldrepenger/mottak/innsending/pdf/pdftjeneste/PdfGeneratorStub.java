package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import java.util.Random;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

@Service
@ConditionalOnMissingBean(PdfGeneratorTjeneste.class)
public class PdfGeneratorStub implements PdfGenerator {

    private static final Random RANDOM = new Random();
    @Override
    public byte[] generate(DokumentBestilling dokument) {
        byte[] result = new byte[1];
        RANDOM.nextBytes(result);
        return result;
    }
}
