package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.ARBEID;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.LOVBESTEMT_FERIE;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.INFOSKRIV_OUTLINE;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.*;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Component
public class InfoskrivRenderer {
    public static final Logger LOG = LoggerFactory.getLogger(InfoskrivRenderer.class);
    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    private static final String NAV_URL = "nav.no/inntektsmelding";
    private static final float STARTY = PdfElementRenderer.calculateStartY();

    public InfoskrivRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    FontAwareCos renderInfoskriv(List<EnkeltArbeidsforhold> arbeidsforhold, Person søker, Søknad søknad,
            FontAwareCos cosOriginal, FontAwarePdfDocument doc) throws IOException {
        if (søknad.getFørsteInntektsmeldingDag() == null) {
            LOG.warn("Ingen førsteInntektsmeldingDag i søknad, dropper infoskriv til bruker.");
            return cosOriginal;
        }

        var navn = textFormatter.navn(søker);
        var datoInntektsmelding = søknad.getFørsteInntektsmeldingDag();
        var ytelse = (Foreldrepenger) søknad.getYtelse();

        var cos = førstesideInfoskriv(doc, cosOriginal);

        float y = STARTY;
        y = header(doc, cos, y);
        y -= addBlankLine();
        y -= renderer.addLeftHeading(txt("infoskriv.header",
                fristTekstFra(datoInntektsmelding)), cos, y);
        y -= addTinyBlankLine();

        if (!erSperreFristPassert(datoInntektsmelding)) {
            y -= renderer.addLineOfRegularText(
                    txt("infoskriv.paragraf1", navn, fristTekstFra(datoInntektsmelding)), cos, y);
        } else {
            y -= renderer.addLineOfRegularText(
                    txt("infoskriv.paragraf1.passert", navn), cos, y);
        }

        y -= addTinyBlankLine();
        y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf2", fornavn(navn), NAV_URL), cos, y);
        y -= addTinyBlankLine();
        y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf3", fornavn(navn)), cos, y);
        y -= addTinyBlankLine();
        y -= addBlankLine();
        y -= renderer.addLeftHeading(txt("infoskriv.opplysningerfrasøknad", navn), cos, y);
        y -= addTinyBlankLine();
        List<String> opplysninger = new ArrayList<>();
        opplysninger.add(txt("infoskriv.arbeidstaker", søker.fnr().value()));
        opplysninger.add(txt("infoskriv.ytelse"));
        opplysninger.add(txt("infoskriv.startdato", formattertDato(søknad.getFørsteUttaksdag())));
        y -= renderer.addLinesOfRegularText(opplysninger, cos, y);
        y -= addBlankLine();

        List<LukketPeriodeMedVedlegg> perioder = sorted(ytelse.fordeling().perioder());
        List<UtsettelsesPeriode> ferieArbeidsperioder = ferieOgArbeid(perioder);

        if (!ferieArbeidsperioder.isEmpty()) {
            PDPage scratch1 = newPage();
            FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
            float x = renderFerieArbeidsperioder(ferieArbeidsperioder, arbeidsforhold, scratchcos, STARTY);
            float behov = STARTY - x;
            if (behov < y) {
                scratchcos.close();
                y = renderFerieArbeidsperioder(ferieArbeidsperioder, arbeidsforhold, cos, y);
            } else {
                cos = førstesideInfoskriv(doc, cos, scratch1, scratchcos);
                y = STARTY - behov;
            }
        }

        List<GradertUttaksPeriode> gradertePerioder = tilGradertePerioder(perioder);

        if (!gradertePerioder.isEmpty()) {
            PDPage scratch1 = newPage();
            FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
            float x = renderGradertePerioder(gradertePerioder, arbeidsforhold, scratchcos, STARTY);
            float behov = STARTY - x;
            if (behov < y) {
                scratchcos.close();
                y = renderGradertePerioder(gradertePerioder, arbeidsforhold, cos, y);
            } else {
                cos = førstesideInfoskriv(doc, cos, scratch1, scratchcos);
                y = STARTY - behov;
            }
        }

        return cos;
    }

    private float renderGradertePerioder(List<GradertUttaksPeriode> gradertePerioder,
            List<EnkeltArbeidsforhold> arbeidsforhold, FontAwareCos cos,
            float y) throws IOException {
        y -= renderer.addLineOfRegularText(txt("svp.kombinertarbeid"), cos, y);
        y -= addTinyBlankLine();
        for (GradertUttaksPeriode periode : gradertePerioder) {
            y -= renderer.addLineOfRegularText(txt("fom", formattertDato(periode.getFom())), cos, y);
            y -= renderer.addLineOfRegularText(txt("tom", formattertDato(periode.getTom())), cos, y);
            if (periode.getVirksomhetsnummer() != null) {
                y -= renderer.addLinesOfRegularText(arbeidsgivere(arbeidsforhold, periode.getVirksomhetsnummer()),
                        cos, y);
            }
            y -= renderer.addLineOfRegularText(txt("arbeidstidprosent",
                    prosentFra(periode.getArbeidstidProsent())), cos, y);
            y -= addTinyBlankLine();
        }
        return y;
    }

    private List<String> arbeidsgivere(List<EnkeltArbeidsforhold> arbeidsforhold, List<String> virksomhetsnummer) {
        return safeStream(arbeidsforhold)
                .filter(a -> virksomhetsnummer.contains(a.getArbeidsgiverId()))
                .map(EnkeltArbeidsforhold::getArbeidsgiverNavn)
                .map(s -> txt("arbeidsgiver", s))
                .toList();
    }

    private float renderFerieArbeidsperioder(List<UtsettelsesPeriode> ferieArbeidsperioder,
            List<EnkeltArbeidsforhold> arbeidsforhold,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLineOfRegularText(txt("svp.utsettelse"), cos, y);
        y -= addTinyBlankLine();
        for (UtsettelsesPeriode periode : ferieArbeidsperioder) {
            y -= renderer.addLineOfRegularText(txt("fom", formattertDato(periode.getFom())), cos, y);
            y -= renderer.addLineOfRegularText(txt("tom", formattertDato(periode.getTom())), cos, y);
            if (periode.getVirksomhetsnummer() != null) {
                y -= renderer.addLinesOfRegularText(arbeidsgivere(arbeidsforhold, periode.getVirksomhetsnummer()),
                        cos, y);
            }
            y -= renderer.addLineOfRegularText(txt("utsettelsesårsak",
                    textFormatter.capitalize(periode.getÅrsak().name())), cos, y);
            y -= addTinyBlankLine();
        }
        y -= addBlankLine();
        return y;
    }

    private float header(FontAwarePdfDocument doc, FontAwareCos cos, float y) throws IOException {
        return y -= renderer.addLogo(doc, cos, y);
    }

    private static String formattertDato(LocalDate date) {
        return date.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.forLanguageTag("nb"))
                .withZone(ZoneId.systemDefault()));
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private static float addTinyBlankLine() {
        return 10;
    }

    private static float addBlankLine() {
        return 20;
    }

    private static String prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::prosent)
                .map(p -> p.intValue() + " %")
                .orElse("Ukjent");
    }

    private static String fristTekstFra(LocalDate datoInntektsmelding) {
        return erSperreFristPassert(datoInntektsmelding) ? "" : " etter " + formattertDato(datoInntektsmelding);
    }

    private static boolean erSperreFristPassert(LocalDate fristDato) {
        return fristDato.isBefore(LocalDate.now().plusDays(1));
    }

    private List<GradertUttaksPeriode> tilGradertePerioder(List<LukketPeriodeMedVedlegg> perioder) {
        return perioder.stream()
                .filter(p -> p instanceof GradertUttaksPeriode)
                .map(GradertUttaksPeriode.class::cast)
                .filter(GradertUttaksPeriode::isErArbeidstaker)
                .toList();
    }

    private List<UtsettelsesPeriode> ferieOgArbeid(List<LukketPeriodeMedVedlegg> periode) {
        return periode.stream()
                .filter(InfoskrivRenderer::isFerieOrArbeid)
                .map(UtsettelsesPeriode.class::cast)
                .toList();
    }

    private static boolean isFerieOrArbeid(LukketPeriodeMedVedlegg periode) {
        if (periode instanceof UtsettelsesPeriode utsettelsesPeriode) {
            var årsak = utsettelsesPeriode.getÅrsak();
            return årsak.equals(LOVBESTEMT_FERIE) || årsak.equals(ARBEID);
        }
        return false;
    }

    private static List<LukketPeriodeMedVedlegg> sorted(List<LukketPeriodeMedVedlegg> perioder) {
        perioder.sort(Comparator.comparing(LukketPeriodeMedVedlegg::getFom));
        return perioder;
    }

    private static FontAwareCos førstesideInfoskriv(FontAwarePdfDocument doc, FontAwareCos cos, PDPage scratch,
            FontAwareCos scratchcos) throws IOException {
        cos.close();
        doc.addPage(scratch);
        cos = scratchcos;
        return cos;
    }

    private FontAwareCos førstesideInfoskriv(FontAwarePdfDocument doc, FontAwareCos cos) throws IOException {
        cos.close();
        PDPage newPage = newPage();
        doc.addPage(newPage);
        renderer.addOutlineItem(doc, newPage, INFOSKRIV_OUTLINE);
        return new FontAwareCos(doc, newPage);
    }

    static String fornavn(String name) {
        return Optional.ofNullable(name)
                .map(String::toLowerCase)
                .map(n -> n.substring(0, n.indexOf(" ")))
                .map(n -> Character.toUpperCase(n.charAt(0)) + n.substring(1))
                .orElse("");
    }
}
