package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class FontAwarePDDocument extends PDDocument {
    public static PDType0Font REGULARFONT;
    public static PDType0Font BOLDFONT;

    public FontAwarePDDocument() throws IOException {
        BOLDFONT = PDType0Font.load(this, new ClassPathResource("/pdf/NotoSans-Bold.ttf").getInputStream());
        REGULARFONT = PDType0Font.load(this, new ClassPathResource("/pdf/NotoSans-Regular.ttf").getInputStream());
    }
}
