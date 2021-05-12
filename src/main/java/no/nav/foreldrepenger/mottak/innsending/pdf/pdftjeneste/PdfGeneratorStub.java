package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import static no.nav.foreldrepenger.boot.conditionals.Cluster.LOCAL;
import static no.nav.foreldrepenger.boot.conditionals.Cluster.TEST;
import static no.nav.foreldrepenger.boot.conditionals.Cluster.VTP;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnClusters;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

@Service
@ConditionalOnClusters(clusters = { TEST, LOCAL, VTP })
public class PdfGeneratorStub implements PdfGenerator {

    @Override
    public byte[] generate(DokumentBestilling dokument) {
        return RandomUtils.nextBytes(1);
    }
}
