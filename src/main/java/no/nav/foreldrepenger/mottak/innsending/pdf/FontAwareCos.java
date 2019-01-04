package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;

public class FontAwareCos {
    final PDFont REGULARFONT;
    final PDFont HEADINGFONT;
    final int REGULARFONTHEIGHT;
    final int HEADINGFONTHEIGHT;
    final int REGULARFONTSIZE = 11;
    private final PDPageContentStream cos;
    private final int HEADINGFONTSIZE = 12;


    public FontAwareCos(FontAwarePDDocument doc, PDPage page) throws IOException {
        this.cos = new PDPageContentStream(doc, page);
        this.REGULARFONT = doc.REGULARFONT;
        this.HEADINGFONT = doc.BOLDFONT;
        this.REGULARFONTHEIGHT = fontHeight(REGULARFONT, REGULARFONTSIZE);
        this.HEADINGFONTHEIGHT = fontHeight(HEADINGFONT, HEADINGFONTSIZE);
    }

    private static int fontHeight(PDFont font, int size) {
        return Math.round((font.getFontDescriptor().getFontBoundingBox().getHeight()) / 1000 * size);
    }

    public void useRegularFont() throws IOException {
        setFont(REGULARFONT, REGULARFONTSIZE);
    }

    public void useHeadingFont() throws IOException {
        setFont(HEADINGFONT, HEADINGFONTSIZE);
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
        return textWidth(string, HEADINGFONT, HEADINGFONTSIZE);
    }

    private float textWidth(String string, PDFont font, int fontSize) throws IOException {
        return font.getStringWidth(string) / 1000 * fontSize;
    }
}
