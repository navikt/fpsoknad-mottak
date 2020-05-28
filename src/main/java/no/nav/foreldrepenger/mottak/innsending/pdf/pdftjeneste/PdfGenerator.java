package no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste;

import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;

public interface PdfGenerator {

    byte[] generate(DokumentBestilling dokument);
}
