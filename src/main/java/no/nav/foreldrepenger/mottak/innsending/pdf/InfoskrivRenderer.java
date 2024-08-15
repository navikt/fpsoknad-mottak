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

import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
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

    FontAwareCos renderInfoskriv(List<EnkeltArbeidsforhold> arbeidsforhold,
                                 Foreldrepenger foreldrepenger,
                                 FontAwareCos cosOriginal,
                                 FontAwarePdfDocument doc,
                                 InnsendingPersonInfo person) throws IOException {
        if (foreldrepenger.getFørsteInntektsmeldingDag() == null) {
            LOG.warn("Ingen førsteInntektsmeldingDag i søknad, dropper infoskriv til bruker.");
            return cosOriginal;
        }

        var fulltNavn = person.navn().navn();
        var formattertFornavn = formattertFornavn(fulltNavn);
        var datoInntektsmelding = foreldrepenger.getFørsteInntektsmeldingDag();

        var cos = førstesideInfoskriv(doc, cosOriginal);

        var y = STARTY;
        y = header(doc, cos, y);
        y -= addBlankLine();
        y -= renderer.addLeftHeading(txt("infoskriv.header", fristTekstFra(datoInntektsmelding)), cos, y);
        y -= addTinyBlankLine();

        if (!erSperreFristPassert(datoInntektsmelding)) {
            y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf1", fulltNavn, fristTekstFra(datoInntektsmelding)), cos, y);
        } else {
            y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf1.passert", fulltNavn), cos, y);
        }

        y -= addTinyBlankLine();
        y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf2", formattertFornavn, NAV_URL), cos, y);
        y -= addTinyBlankLine();
        y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf3", formattertFornavn), cos, y);
        y -= addTinyBlankLine();
        y -= addBlankLine();
        y -= renderer.addLeftHeading(txt("infoskriv.opplysningerfrasøknad", fulltNavn), cos, y);
        y -= addTinyBlankLine();
        List<String> opplysninger = new ArrayList<>();
        opplysninger.add(txt("infoskriv.arbeidstaker", person.fnr().value()));
        opplysninger.add(txt("infoskriv.ytelse"));
        opplysninger.add(txt("infoskriv.startdato", formattertDato(foreldrepenger.getFørsteUttaksdag())));
        y -= renderer.addLinesOfRegularText(opplysninger, cos, y);
        y -= addBlankLine();

        var perioder = sorted(foreldrepenger.fordeling().perioder());
        var ferieArbeidsperioder = ferieOgArbeid(perioder);

        if (!ferieArbeidsperioder.isEmpty()) {
            var scratch1 = newPage();
            var scratchcos = new FontAwareCos(doc, scratch1);
            var x = renderFerieArbeidsperioder(ferieArbeidsperioder, scratchcos, STARTY);
            var behov = STARTY - x;
            if (behov < y) {
                scratchcos.close();
                y = renderFerieArbeidsperioder(ferieArbeidsperioder, cos, y);
            } else {
                cos = førstesideInfoskriv(doc, cos, scratch1, scratchcos);
                y = STARTY - behov;
            }
        }

        var gradertePerioder = tilGradertePerioder(perioder);

        if (!gradertePerioder.isEmpty()) {
            var scratch1 = newPage();
            var scratchcos = new FontAwareCos(doc, scratch1);
            var x = renderGradertePerioder(gradertePerioder, arbeidsforhold, scratchcos, STARTY);
            var behov = STARTY - x;
            if (behov < y) {
                scratchcos.close();
                renderGradertePerioder(gradertePerioder, arbeidsforhold, cos, y);
            } else {
                cos = førstesideInfoskriv(doc, cos, scratch1, scratchcos);
            }
        }

        return cos;
    }

    private float renderGradertePerioder(List<GradertUttaksPeriode> gradertePerioder,
                                         List<EnkeltArbeidsforhold> arbeidsforhold,
                                         FontAwareCos cos,
                                         float y) throws IOException {
        y -= renderer.addLineOfRegularText(txt("svp.kombinertarbeid"), cos, y);
        y -= addTinyBlankLine();
        for (var periode : gradertePerioder) {
            y -= renderer.addLineOfRegularText(txt("fom", formattertDato(periode.getFom())), cos, y);
            y -= renderer.addLineOfRegularText(txt("tom", formattertDato(periode.getTom())), cos, y);
            if (periode.getVirksomhetsnummer() != null) {
                y -= renderer.addLinesOfRegularText(arbeidsgivere(arbeidsforhold, periode.getVirksomhetsnummer()), cos, y);
            }
            y -= renderer.addLineOfRegularText(txt("arbeidstidprosent", prosentFra(periode.getArbeidstidProsent())), cos, y);
            y -= addTinyBlankLine();
        }
        return y;
    }

    private List<String> arbeidsgivere(List<EnkeltArbeidsforhold> arbeidsforhold, List<String> virksomhetsnummer) {
        return safeStream(arbeidsforhold).filter(a -> virksomhetsnummer.contains(a.arbeidsgiverId()))
            .map(EnkeltArbeidsforhold::arbeidsgiverNavn)
            .map(s -> txt("arbeidsgiver", s))
            .toList();
    }

    private float renderFerieArbeidsperioder(List<UtsettelsesPeriode> ferieArbeidsperioder, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLineOfRegularText(txt("svp.utsettelse"), cos, y);
        y -= addTinyBlankLine();
        for (var periode : ferieArbeidsperioder) {
            y -= renderer.addLineOfRegularText(txt("fom", formattertDato(periode.getFom())), cos, y);
            y -= renderer.addLineOfRegularText(txt("tom", formattertDato(periode.getTom())), cos, y);
            y -= renderer.addLineOfRegularText(txt("utsettelsesårsak", textFormatter.capitalize(periode.getÅrsak().name())), cos, y);
            y -= addTinyBlankLine();
        }
        y -= addBlankLine();
        return y;
    }

    private float header(FontAwarePdfDocument doc, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLogo(doc, cos, y);
        return y;
    }

    private static String formattertDato(LocalDate date) {
        return date.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.forLanguageTag("nb")).withZone(ZoneId.systemDefault()));
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
        return Optional.ofNullable(prosent).map(ProsentAndel::prosent).map(p -> p.intValue() + " %").orElse("Ukjent");
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
        return periode.stream().filter(InfoskrivRenderer::isFerieOrArbeid).map(UtsettelsesPeriode.class::cast).toList();
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

    private static FontAwareCos førstesideInfoskriv(FontAwarePdfDocument doc,
                                                    FontAwareCos cos,
                                                    PDPage scratch,
                                                    FontAwareCos scratchcos) throws IOException {
        cos.close();
        doc.addPage(scratch);
        cos = scratchcos;
        return cos;
    }

    private FontAwareCos førstesideInfoskriv(FontAwarePdfDocument doc, FontAwareCos cos) throws IOException {
        cos.close();
        var newPage = newPage();
        doc.addPage(newPage);
        renderer.addOutlineItem(doc, newPage, INFOSKRIV_OUTLINE);
        return new FontAwareCos(doc, newPage);
    }

    static String formattertFornavn(String name) {
        return Optional.ofNullable(name)
            .map(String::toLowerCase)
            .map(n -> n.substring(0, n.indexOf(" ")))
            .map(n -> Character.toUpperCase(n.charAt(0)) + n.substring(1))
            .orElse("");
    }
}
