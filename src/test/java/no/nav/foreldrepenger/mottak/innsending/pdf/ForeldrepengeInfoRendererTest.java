package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ForeldrepengeInfoRendererTest {

    @Test
    public void charsNotRepresentedInFontGlyphStrippedOrReplaced() {
        List<String> dirtyText = Arrays.asList(
            "\u0141aslo \u0142asaron får sine bokstaver byttet om",
            "Left-to-right override strippes \u202D helt ut av teksten",
            "Tab\tog andre blanke tegn\u00A0erstattes med space",
            "Díacriter érstattes ãv sinë kjédelige varianter",
            "Albert Åberg og/å Prøysen beholder sine nordiske tegn, mens replacement character fjernes\uFFFD");
        List<String> cleanText = Arrays.asList(
            "Laslo lasaron får sine bokstaver byttet om",
            "Left-to-right override strippes  helt ut av teksten",
            "Tab og andre blanke tegn erstattes med space",
            "Diacriter erstattes av sine kjedelige varianter",
            "Albert Åberg og/å Prøysen beholder sine nordiske tegn, mens replacement character fjernes");

        //assertEquals(cleanText, ForeldrepengeInfoRenderer.normalize(dirtyText));
    }

}
