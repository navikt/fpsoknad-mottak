package no.nav.foreldrepenger.mottak.pdf;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;

@Component
public class ForeldrepengerPDFGenerator extends AbstractPDFGenerator {

    public byte[] generate(Søknad søknad) {
        try {
            Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
            Medlemsskap medlemsskap = stønad.getMedlemsskap();
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            logo(document);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
