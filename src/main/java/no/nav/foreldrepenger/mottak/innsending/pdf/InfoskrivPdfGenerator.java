package no.nav.foreldrepenger.mottak.innsending.pdf;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

@Service
public class InfoskrivPdfGenerator {
    public static final Logger LOG = LoggerFactory.getLogger(InfoskrivPdfGenerator.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");
    private static final String ALTINN_URL1 = "https://altinn.no/skjemaoversikt/arbeids--og-velferdsetaten-";
    private static final String ALTINN_URL2 = "nav/Inntektsmelding-til-NAV/";
    private static final String NAV_URL = "nav.no/inntektsmelding";

    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    private static final float STARTY = PDFElementRenderer.calculateStartY();

    @Inject
    public InfoskrivPdfGenerator(PDFElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    public byte[] generate(Søknad søknad, Person søker) {

        try (FontAwarePDDocument doc = new FontAwarePDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Foreldrepenger ytelse = (Foreldrepenger) søknad.getYtelse();
            String navn = textFormatter.navn(søker);

            PDPage page = newPage();
            doc.addPage(page);
            FontAwareCos cos = new FontAwareCos(doc, page);
            float y = STARTY;
            y = header(doc, cos, y);
            float headerSize = STARTY - y;
            y -= renderer.addLeftHeading(
                txt("infoskriv.header",
                    textFormatter.navn(søker)), cos, y); // add dato fom
            y -= renderer.addLineOfRegularText(txt("infoskriv.søkerdata", navn), cos, y);
            y -= tinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv1", FMT.format(søknad.getFørsteUttaksdag())), cos, y);
            y -= tinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv2"), cos, y);
            y -= tinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv3"), cos, y);
            y -= renderer.addLineOfRegularText(ALTINN_URL1, cos, y);
            y -= renderer.addLineOfRegularText(ALTINN_URL2, cos, y);
            y -= tinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv4", NAV_URL), cos, y);
            y -= tinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv5"), cos, y);
            y -= renderer.addDividerLine(cos, y);
            y -= tinyBlankLine();
            y -= renderer.addLeftHeading(txt("infoskriv.opplysningerfrasøknad", navn), cos, y);

            List<String> opplysninger = new ArrayList<>();
            opplysninger.add(txt("infoskriv.arbeidstaker", søker.fnr.getFnr()));
            opplysninger.add(txt("infoskriv.ytelse"));
            opplysninger.add(txt("infoskriv.startdato", FMT.format(søknad.getFørsteUttaksdag())));

            y -= renderer.addLinesOfRegularText(opplysninger, cos, y);

            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            LOG.warn("Generering av PDF til arbeidsgiver feilet", e);
            return null;
        }
    }

    private float header(FontAwarePDDocument doc, FontAwareCos cos, float y) throws IOException {
        return y-= renderer.addLogo(doc, cos, y);
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private float tinyBlankLine() {
        return 10;
    }



}
