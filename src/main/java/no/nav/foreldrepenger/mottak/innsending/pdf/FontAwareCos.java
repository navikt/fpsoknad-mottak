package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;

public class FontAwareCos {
    private FontAwarePDDocument doc;
    private final PDFont regularFont;
    private final PDFont boldFont;
    private PDPage page;
    private PDPageContentStream cos;
    public final int REGULARFONTSIZE = 12;
    public final int REGULARFONTHEIGHT;
    public final int HEADINGFONTSIZE = 13;
    public final int HEADINGFONTHEIGHT;


    public FontAwareCos(FontAwarePDDocument doc, PDPage page) throws IOException {
        this.doc = doc;
        this.page = page;
        this.cos = new PDPageContentStream(doc, page);
        this.regularFont = doc.getRegularFont();
        this.boldFont = doc.getBoldFont();
        REGULARFONTHEIGHT = fontHeight(regularFont, REGULARFONTSIZE);
        HEADINGFONTHEIGHT = fontHeight(boldFont, HEADINGFONTSIZE);
    }

    public void useRegularFont() throws IOException {
        setFont(regularFont, REGULARFONTSIZE);
    }

    public void useBoldFont() throws IOException {
        setFont(boldFont, HEADINGFONTSIZE);
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

    public PDFont getRegularFont() {
        return regularFont;
    }

    private static int fontHeight(PDFont font, int size) {
        return Math.round((font.getFontDescriptor().getFontBoundingBox().getHeight()) / 1000 * size);
    }

    public float boldTextWidth(String string) throws IOException {
        return textWidth(string, boldFont, HEADINGFONTSIZE);
    }

    public float plainTextWidth(String string) throws  IOException {
        return textWidth(string, regularFont, REGULARFONTSIZE);
    }

    private float textWidth(String string, PDFont font, int fontSize) throws IOException {
        return font.getStringWidth(string) / 1000 * fontSize;
    }
}
