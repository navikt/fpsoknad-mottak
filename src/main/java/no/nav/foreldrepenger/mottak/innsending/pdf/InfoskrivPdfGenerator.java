package no.nav.foreldrepenger.mottak.innsending.pdf;

import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.felles.Person;

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

    public byte[] generate(Kvittering kvittering, Person søker) {
        try (FontAwarePDDocument doc = new FontAwarePDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String navn = textFormatter.navn(søker);
            LocalDate datoInntektsmelding = kvittering.getFørsteInntektsmeldingDag();
            PDPage page = newPage();
            doc.addPage(page);
            FontAwareCos cos = new FontAwareCos(doc, page);
            float y = STARTY;
            y = header(doc, cos, y);
            y -= addBlankLine();
            y -= renderer.addLeftHeading(txt("infoskriv.header",
                    tilFristTekst(datoInntektsmelding)), cos, y);
            y -= addTinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf1",
                    navn, tilFristTekst(datoInntektsmelding)), cos, y);
            y -= addTinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf2", NAV_URL), cos, y);
            y -= addTinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf3"), cos, y);
            y -= addTinyBlankLine();
            y -= addBlankLine();
            y -= renderer.addLeftHeading(txt("infoskriv.opplysningerfrasøknad", navn), cos, y);
            y -= addTinyBlankLine();
            List<String> opplysninger = new ArrayList<>();
            opplysninger.add(txt("infoskriv.arbeidstaker", søker.getFnr().getFnr()));
            opplysninger.add(txt("infoskriv.ytelse"));
            opplysninger.add(txt("infoskriv.startdato", FMT.format(kvittering.getFørsteDag())));
            y -= renderer.addLinesOfRegularText(opplysninger, cos, y);
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            LOG.warn("Generering av infoskrivPdf til arbeidsgiver feilet, ignorerer feil", e);
            return null;
        }
    }

    private float header(FontAwarePDDocument doc, FontAwareCos cos, float y) throws IOException {
        return y -= renderer.addLogo(doc, cos, y);
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private float addTinyBlankLine() {
        return 10;
    }

    private float addBlankLine() {
        return 20;
    }

    private String tilFristTekst(LocalDate dato) {
        if (dato.isBefore(LocalDate.now())) {
            return "";
        }
        return " etter " + FMT.format(dato);
    }



}
