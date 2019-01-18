package no.nav.foreldrepenger.mottak.innsending.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class FontAwareCos {
    final PDFont fontRegular;
    final PDFont fontHeading;
    final int fontHeightRegular;
    final int fontHeightHeading;
    final int REGULARFONTSIZE = 11;
    private final PDPageContentStream cos;
    private final int HEADINGFONTSIZE = 12;

    public FontAwareCos(FontAwarePDDocument doc, PDPage page) throws IOException {
        this.cos = new PDPageContentStream(doc, page);
        this.fontRegular = doc.getRegularFont();
        this.fontHeading = doc.getBoldFont();
        this.fontHeightRegular = fontHeight(fontRegular, REGULARFONTSIZE);
        this.fontHeightHeading = fontHeight(fontHeading, HEADINGFONTSIZE);
    }

    private static int fontHeight(PDFont font, int size) {
        return Math.round(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * size);
    }

    public void useRegularFont() throws IOException {
        setFont(fontRegular, REGULARFONTSIZE);
    }

    public void useHeadingFont() throws IOException {
        setFont(fontHeading, HEADINGFONTSIZE);
    }

    public PDPageContentStream getCos() {
        return cos;
    }

    public void beginText() throws IOException {
        cos.beginText();
    }

    public void newLineAtOffset(float tx, float ty) throws IOException {
        cos.newLineAtOffset(tx, ty);
    }

    public void showText(String string) throws IOException {
        cos.showText(string);
    }

    public void endText() throws IOException {
        cos.endText();
    }

    public void close() throws IOException {
        cos.close();
    }

    private void setFont(PDFont font, float fontSize) throws IOException {
        cos.setFont(font, fontSize);
    }

    public float headingTextWidth(String string) throws IOException {
        return textWidth(string, fontHeading, HEADINGFONTSIZE);
    }

    private static float textWidth(String string, PDFont font, int fontSize) throws IOException {
        return font.getStringWidth(string) / 1000 * fontSize;
    }
}
