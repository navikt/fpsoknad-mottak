package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

@Service
@ConditionalOnMissingBean(PdfGeneratorTjeneste.class)
public class PdfGeneratorStub implements PdfGenerator {

    @Override
    public byte[] generate(DokumentBestilling dokument) {
        return RandomUtils.nextBytes(1);
    }
}
