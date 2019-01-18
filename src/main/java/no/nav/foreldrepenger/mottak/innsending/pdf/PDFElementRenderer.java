
package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.text.Normalizer.Form.NFD;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class PDFElementRenderer {

    public static final Logger LOG = LoggerFactory.getLogger(PDFElementRenderer.class);

    private static final byte[] NAV_LOGO = logo();

    private static final int MARGIN = 40;

    private static final PDRectangle MEDIABOX = new PDPage(A4).getMediaBox();

    public static float calculateStartY() {
        return MEDIABOX.getUpperRightY() - MARGIN;
    }

    int characterLimitInCos(PDFont font, int fontSize, int marginOffset) {
        float maxLineWidth = MEDIABOX.getWidth() - 2 * MARGIN - marginOffset;
        float avgCharWidth = font.getAverageFontWidth() / 1000 * fontSize;
        return Math.round(maxLineWidth / avgCharWidth);
    }

    List<String> splitLineIfNecessary(String str, int maxLength) {
        List<String> lineList = new ArrayList<>();
        if (str.length() > maxLength) {
            String candidateLine = str.substring(0, maxLength);
            boolean noSpaceOrLastSpaceInFirstHalf = candidateLine.lastIndexOf(" ") < maxLength / 2;
            int lastSpace = candidateLine.lastIndexOf(" ");
            String line;
            String remainingStr;

            if (noSpaceOrLastSpaceInFirstHalf) {
                line = str.substring(0, maxLength) + "-";
                remainingStr = str.substring(maxLength);
            }
            else {
                line = str.substring(0, lastSpace);
                remainingStr = str.substring(lastSpace + 1);
            }
            lineList.add(line);
            lineList.addAll(splitLineIfNecessary(remainingStr, maxLength));

            return lineList;
        }
        return Arrays.asList(str);
    }

    public float addLineOfRegularText(String line, FontAwareCos cos, float startY) throws IOException {
        return addLineOfRegularText(0, line, cos, startY);
    }

    public float addLineOfRegularText(int marginOffset, String line, FontAwareCos cos, float startY)
            throws IOException {
        String encodableLine = normalizeAndRemoveNonencodableChars(line, cos.REGULARFONT);
        List<String> lines = splitLineIfNecessary(
                encodableLine,
                characterLimitInCos(cos.REGULARFONT, cos.REGULARFONTSIZE, marginOffset));
        int lineNumber = 0;
        for (String singleLine : lines) {
            cos.beginText();
            cos.useRegularFont();
            cos.newLineAtOffset(MARGIN + marginOffset, startY - lineNumber * cos.REGULARFONTHEIGHT);
            cos.showText(Optional.ofNullable(singleLine).orElse(""));
            cos.endText();
            lineNumber++;
        }
        return cos.REGULARFONTHEIGHT * lines.size();
    }

    String normalizeAndRemoveNonencodableChars(String str, PDFont font) throws IOException {
        return removeNonencodableChars(normalizeString(str), font);
    }

    private static String removeNonencodableChars(String string, PDFont font) throws IOException {
        StringBuilder encodable = new StringBuilder();
        for (char character : string.toCharArray()) {
            if (isCharacterEncodeable(character, font)) {
                encodable.append(character);
            }
        }
        return encodable.toString();
    }

    private static boolean isCharacterEncodeable(char character, PDFont font) throws IOException {
        try {
            font.encode(Character.toString(character));
            return true;
        } catch (IllegalArgumentException e) { // likely non-existence of glyph
            LOG.info(String.format(
                    "No glyph for %x in font %s, character has been dropped from pdf.",
                    (int) character, font.toString()));
            return false;
        }
    }

    private static String normalizeString(String str) {
        return Stream.of(str)
                .map(s -> s.replaceAll("å", "xxxxxxxxxx"))
                .map(s -> s.replaceAll("Å", "XXXXXXXXXX"))
                .map(s -> Normalizer.normalize(s, NFD))
                .map(s -> s.replaceAll("[\\p{Blank}\u00A0]", " ")) // replace tab/no-break space with space
                .map(s -> s.replaceAll("[\u202D\uFFFD]", "")) // strip left-to-right-operator/not defined
                .map(s -> s.replaceAll("xxxxxxxxxx", "å"))
                .map(s -> s.replaceAll("XXXXXXXXXX", "Å"))
                .collect(Collectors.joining());
    }

    public float addLinesOfRegularText(List<String> lines, FontAwareCos cos, float startY)
            throws IOException {
        return addLinesOfRegularText(0, lines, cos, startY);

    }

    public float addLinesOfRegularText(int marginOffset, List<String> lines, FontAwareCos cos, float startY)
            throws IOException {
        float yTotal = 0;
        for (String line : lines) {
            yTotal += addLineOfRegularText(marginOffset, line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addBulletPoint(int offset, String line, FontAwareCos cos, float startY) throws IOException {
        return addLineOfRegularText(offset, "\u2022 " + line, cos, startY);
    }

    public float addLMultilineBulletpoint(int offset, List<String> lines, FontAwareCos cos, float startY)
            throws IOException {
        float yTotal = addBulletPoint(offset, lines.get(0), cos, startY);
        for (String line : lines.subList(1, lines.size())) {
            yTotal += addLineOfRegularText("  " + line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addBulletList(int offset, List<String> lines, FontAwareCos cos, float startY)
            throws IOException {
        float yTotal = 0;
        for (String line : lines) {
            yTotal += addBulletPoint(offset, line, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addCenteredHeading(String heading, FontAwareCos cos, float startY) throws IOException {
        cos.beginText();
        cos.useHeadingFont();
        float titleWidth = cos.headingTextWidth(heading);
        float startX = (MEDIABOX.getWidth() - titleWidth) / 2;
        cos.newLineAtOffset(startX, startY);
        cos.showText(normalizeAndRemoveNonencodableChars(heading, cos.HEADINGFONT));
        cos.endText();
        return cos.HEADINGFONTHEIGHT;
    }

    public float addCenteredHeadings(List<String> headings, FontAwareCos cos, float startY) throws IOException {
        float yTotal = 0;
        for (String heading : headings) {
            yTotal += addCenteredHeading(heading, cos, startY - yTotal);
        }
        return yTotal;
    }

    public float addLeftHeading(String heading, FontAwareCos cos, float startY) throws IOException {
        cos.beginText();
        cos.useHeadingFont();
        float startX = MARGIN;
        cos.newLineAtOffset(startX, startY);
        cos.showText(heading);
        cos.endText();
        return cos.HEADINGFONTHEIGHT;
    }

    public float addDividerLine(FontAwareCos cos, float startY) throws IOException {
        cos.getCos().setLineWidth(1);
        cos.getCos().moveTo(MARGIN, startY);
        cos.getCos().lineTo(MEDIABOX.getWidth() - MARGIN, startY);
        cos.getCos().closeAndStroke();
        return 20;
    }

    public float addLogo(PDDocument doc, FontAwareCos cos, float startY) throws IOException {
        PDImageXObject ximage = PDImageXObject.createFromByteArray(doc, NAV_LOGO, "logo");
        float startX = (MEDIABOX.getWidth() - 99) / 2;
        float offsetTop = 40;
        startY -= 62 / 2 + offsetTop;
        cos.getCos().drawImage(ximage, startX, startY, 99, 62);
        return 62 + offsetTop;
    }

    public float addBlankLine() {
        return 20;
    }

    private static byte[] logo() {
        try {
            return StreamUtils.copyToByteArray(new ClassPathResource("/pdf/nav-logo_alphaless.png").getInputStream());
        } catch (IOException ex) {
            throw new RuntimeException("Error while reading image", ex);
        }
    }

    public float addBulletList(List<String> lines, FontAwareCos cos, float y) throws IOException {
        return addBulletList(0, lines, cos, y);
    }

    public float addBulletPoint(String line, FontAwareCos cos, float y) throws IOException {
        return addBulletPoint(0, line, cos, y);
    }

    public float addLMultilineBulletpoint(List<String> lines, FontAwareCos cos, float y) throws IOException {
        return addLMultilineBulletpoint(0, lines, cos, y);
    }
}
