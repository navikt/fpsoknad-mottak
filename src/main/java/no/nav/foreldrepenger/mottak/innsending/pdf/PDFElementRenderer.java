package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.text.Normalizer.Form.NFD;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

public class PDFElementRenderer {

    private static final byte[] NAV_LOGO = logo();

    private static final int MARGIN = 40;

    private static final PDFont FONTPLAIN = PDType1Font.HELVETICA;
    private static final PDFont FONTBOLD = PDType1Font.HELVETICA_BOLD;

    private static final int FONTPLAINSIZE = 12;
    private static final int FONTHEADINGSIZE = 13;
    private static final int FONTPLAINHEIGHT = fontPlainHeight();
    private static final int FONTHEADINGHEIGHT = fontHeadingHeight();

    private static final PDRectangle MEDIABOX = new PDPage(A4).getMediaBox();

    public PDPage newPage() {
        return new PDPage(A4);
    }

    public static float calculateStartY() {
        return MEDIABOX.getUpperRightY() - MARGIN;
    }

    public float addLineOfRegularText(String line, PDPageContentStream cos, float startY) throws IOException {
        return addLineOfRegularText(0, line, cos, startY);
    }

    public float addLineOfRegularText(int marginOffset, String line, PDPageContentStream cos, float startY)
            throws IOException {
        cos.beginText();
        cos.setFont(FONTPLAIN, FONTPLAINSIZE);
        cos.newLineAtOffset(MARGIN + marginOffset, startY);
        cos.showText(Optional.ofNullable(normalize(line)).orElse(""));
        cos.endText();
        return FONTPLAINHEIGHT;
    }

    private static String normalize(String line) {
        return Normalizer.normalize(line, NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    public float addLinesOfRegularText(List<String> lines, PDPageContentStream cos, float startY)
            throws IOException {
        return addLinesOfRegularText(0, lines, cos, startY);

    }

    public float addLinesOfRegularText(int marginOffset, List<String> lines, PDPageContentStream cos, float startY)
            throws IOException {
        float yTotal = 0;
        for (String line : lines) {
            yTotal += addLineOfRegularText(marginOffset, line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addBulletPoint(int offset, String line, PDPageContentStream cos, float startY) throws IOException {
        return addLineOfRegularText(offset, "\u2022 " + line, cos, startY);
    }

    public float addLMultilineBulletpoint(int offset, List<String> lines, PDPageContentStream cos, float startY)
            throws IOException {
        float yTotal = addBulletPoint(offset, lines.get(0), cos, startY);
        for (String line : lines.subList(1, lines.size())) {
            yTotal += addLineOfRegularText("  " + line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addBulletList(int offset, List<String> lines, PDPageContentStream cos, float startY)
            throws IOException {
        float yTotal = 0;
        for (String line : lines) {
            yTotal += addBulletPoint(offset, line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addCenteredHeading(String heading, PDPageContentStream cos, float startY) throws IOException {
        cos.beginText();
        cos.setFont(FONTBOLD, FONTHEADINGSIZE);
        float titleWidth = FONTBOLD.getStringWidth(heading) / 1000 * FONTHEADINGSIZE;
        float startX = (MEDIABOX.getWidth() - titleWidth) / 2;
        cos.newLineAtOffset(startX, startY);
        cos.showText(heading);
        cos.endText();
        return FONTHEADINGHEIGHT;
    }

    public float addCenteredHeadings(List<String> headings, PDPageContentStream cos, float startY) throws IOException {
        float yTotal = 0;
        for (String heading : headings) {
            yTotal += addCenteredHeading(heading, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addLeftHeading(String heading, PDPageContentStream cos, float startY) throws IOException {
        cos.beginText();
        cos.setFont(FONTBOLD, FONTHEADINGSIZE);
        float startX = MARGIN;
        cos.newLineAtOffset(startX, startY);
        cos.showText(heading);
        cos.endText();
        return FONTHEADINGHEIGHT;
    }

    public float addDividerLine(PDPageContentStream cos, float startY) throws IOException {
        cos.setLineWidth(1);
        cos.moveTo(MARGIN, startY);
        cos.lineTo(MEDIABOX.getWidth() - MARGIN, startY);
        cos.closeAndStroke();
        return 20;
    }

    public float addLogo(PDDocument doc, PDPageContentStream cos, float startY) throws IOException {
        PDImageXObject ximage = PDImageXObject.createFromByteArray(doc, NAV_LOGO, "logo");
        float startX = (MEDIABOX.getWidth() - ximage.getWidth()) / 2;
        float offsetTop = 40;
        startY -= ximage.getHeight() / 2 + offsetTop;
        cos.drawImage(ximage, startX, startY, ximage.getWidth(), ximage.getHeight());
        return ximage.getHeight() + offsetTop;
    }

    public float addBlankLine() {
        return 20;
    }

    private static byte[] logo() {
        try {
            return StreamUtils.copyToByteArray(new ClassPathResource("/pdf/nav-logo.png").getInputStream());
        } catch (IOException ex) {
            throw new RuntimeException("Error while reading image", ex);
        }
    }

    private static int fontHeadingHeight() {
        return Math.round(FONTPLAIN.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * FONTHEADINGSIZE);
    }

    private static int fontPlainHeight() {
        return Math.round(FONTPLAIN.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * FONTPLAINSIZE);
    }

    public float addBulletList(List<String> lines, PDPageContentStream cos, float y) throws IOException {
        return addBulletList(0, lines, cos, y);
    }

    public float addBulletPoint(String line, PDPageContentStream cos, float y) throws IOException {
        return addBulletPoint(0, line, cos, y);
    }

    public float addLMultilineBulletpoint(List<String> lines, PDPageContentStream cos, float y) throws IOException {
        return addLMultilineBulletpoint(0, lines, cos, y);
    }
}
