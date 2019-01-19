package no.nav.foreldrepenger.mottak.innsending.pdf;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class PDFElementRendererTest {

    @Test
    public void softLineBreakOnSpace() {
        List<String> lines = splitLine("test1 test2", 8);
        assertEquals("test1", lines.get(0));
        assertEquals("test2", lines.get(1));
    }

    @Test
    public void hardLineBreak() {
        List<String> linesA = splitLine("test1 test2test3", 10);
        assertEquals("test1", linesA.get(0));
        assertEquals("test2test3", linesA.get(1));

        List<String> linesB = splitLine("test1 test2test3test4", 17);
        assertEquals("test1 test2test3t-", linesB.get(0));
        assertEquals("est4", linesB.get(1));
    }

    private static List<String> splitLine(String str, int maxLength) {
        return new PDFElementRenderer().splitLineIfNecessary(str, maxLength);
    }

    @Test
    public void charsNotRepresentedInFontGlyphStrippedOrReplaced() throws IOException {

        PDDocument pdf = new PDDocument();
        PDFont font = PDType0Font.load(pdf, new ClassPathResource("/pdf/NotoSans-Bold.ttf").getInputStream());

        String dirtyText = "Left-to-right override strippes \u202D helt ut av teksten" +
                "Tab\tog andre blanke tegn\u00A0erstattes med space" +
                "ikke-eksisterende \u2f8a glypher erstattes med space" +
                "Albert Åberg og/å Prøysen beholder sine nordiske tegn, mens replacement character fjernes\uFFFD";
        String cleanText = "Left-to-right override strippes  helt ut av teksten" +
                "Tab og andre blanke tegn erstattes med space" +
                "ikke-eksisterende  glypher erstattes med space" +
                "Albert Åberg og/å Prøysen beholder sine nordiske tegn, mens replacement character fjernes";

        PDFElementRenderer renderer = new PDFElementRenderer();
        assertEquals(cleanText, renderer.normalizeAndRemoveNonencodableChars(dirtyText, font));
    }

}
