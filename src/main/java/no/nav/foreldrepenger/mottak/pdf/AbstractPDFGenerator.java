package no.nav.foreldrepenger.mottak.pdf;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;

public abstract class AbstractPDFGenerator {

    protected static void logo(Document document)
            throws BadElementException, MalformedURLException, IOException, DocumentException {
        Image logo = logo();
        logo.setAlignment(Image.ALIGN_CENTER);
        document.add(logo);
    }

    private static Image logo() throws BadElementException, MalformedURLException, IOException {
        return Image.getInstance(
                StreamUtils.copyToByteArray(new ClassPathResource("pdf/nav-logo.png").getInputStream()));
    }

}
