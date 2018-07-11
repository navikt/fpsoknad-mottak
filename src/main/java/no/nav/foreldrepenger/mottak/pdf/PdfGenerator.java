package no.nav.foreldrepenger.mottak.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    private static final String logoPath = PdfGenerator.class
        .getClassLoader().getResource("pdf/nav-logo.png").getPath();

    private static final int margin = 40;

    private static final PDFont fontPlain = PDType1Font.HELVETICA;
    private static final PDFont fontBold = PDType1Font.HELVETICA_BOLD;

    private static final int fontPLainSize = 12;
    private static final int fontHeadingSize = 16;
    private static final int fontPLainHeight =
        Math.round(fontPlain.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontPLainSize);
    private static final int fontHeadingHeight =
        Math.round(fontPlain.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontHeadingSize);

    private static final PDRectangle mediaBox = new PDPage(PDRectangle.A4).getMediaBox();

    public PDPage newPage() {
        return new PDPage(PDRectangle.A4);
    }

    public static float calculateStartY() {
        return mediaBox.getUpperRightY() - margin;
    }

    public float addLineOfRegularText(String line, PDPageContentStream cos, float startY) throws IOException {
        cos.beginText();
        cos.setFont(fontPlain, fontPLainSize);
        cos.newLineAtOffset(margin, startY);
        cos.showText(line);
        cos.endText();
        return fontPLainHeight;
    }

    public float addLinesOfRegularText(List<String> lines, PDPageContentStream cos, float startY) throws IOException {
        float yTotal = 0;
        for (String line : lines) {
            yTotal += addLineOfRegularText(line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addBulletPoint(String line, PDPageContentStream cos, float startY) throws IOException {
        return addLineOfRegularText("\u2022 " + line, cos, startY);
    }

    public float addBulletList(List<String> lines, PDPageContentStream cos, float startY) throws IOException {
        float yTotal = 0;
        for (String line : lines) {
            yTotal += addBulletPoint(line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addCenteredHeading(String heading, PDPageContentStream cos, float startY) throws IOException {
        cos.beginText();
        cos.setFont(fontBold, fontHeadingSize);
        float titleWidth = fontBold.getStringWidth(heading) / 1000 * fontHeadingSize;
        float startX = (mediaBox.getWidth() - titleWidth) / 2;
        cos.newLineAtOffset(startX, startY);
        cos.showText(heading);
        cos.endText();
        return fontHeadingHeight;
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
        cos.setFont(fontBold, fontHeadingSize);
        float startX = margin;
        cos.newLineAtOffset(startX, startY);
        cos.showText(heading);
        cos.endText();
        return fontHeadingHeight;
    }

    public float addDividerLine(PDPageContentStream cos, float startY) throws IOException {
        cos.setLineWidth(1);
        cos.moveTo(margin, startY);
        cos.lineTo(mediaBox.getWidth() - margin, startY);
        cos.closeAndStroke();
        return 20;
    }

    public float addLogo(PDDocument doc, PDPageContentStream cos, float startY) throws IOException {
        PDImageXObject ximage = PDImageXObject.createFromFile(logoPath, doc);
        float startX = (mediaBox.getWidth() - ximage.getWidth()) / 2;
        float offsetTop = 40;
        startY -= ximage.getHeight() / 2 + offsetTop;
        cos.drawImage(ximage, startX, startY, ximage.getWidth(), ximage.getHeight());
        return ximage.getHeight() + offsetTop;
    }

    public float addBlankLine() {
        return 20;
    }

}
