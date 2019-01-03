package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class FontAwarePDDocument extends PDDocument {
    private final PDType0Font regularFont;
    private final PDType0Font boldFont;

    public FontAwarePDDocument() throws IOException {
        boldFont = PDType0Font.load(this, new ClassPathResource("/pdf/NotoSans-Bold.ttf").getInputStream());
        regularFont = PDType0Font.load(this, new ClassPathResource("/pdf/NotoSans-Regular.ttf").getInputStream());
    }

    public PDType0Font getRegularFont() {
        return regularFont;
    }

    public PDType0Font getBoldFont() {
        return boldFont;
    }
}
