package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.ARBEID;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.LOVBESTEMT_FERIE;
import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.INFOSKRIV_OUTLINE;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.Arbeidsforhold;

@Component
public class InfoskrivRenderer {
    public static final Logger LOG = LoggerFactory.getLogger(InfoskrivRenderer.class);
    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    private static final String NAV_URL = "nav.no/inntektsmelding";
    private static final float STARTY = PDFElementRenderer.calculateStartY();

    public InfoskrivRenderer(PDFElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    FontAwareCos renderInfoskriv(List<Arbeidsforhold> arbeidsforhold, Person søker, Søknad søknad,
            FontAwareCos cosOriginal, FontAwarePDDocument doc) throws IOException {
        if (søknad.getFørsteInntektsmeldingDag() == null) {
            LOG.warn("Ingen førsteInntektsmeldingDag i søknad, dropper infoskriv til bruker.");
            return cosOriginal;
        }

        String navn = textFormatter.navn(søker);
        LocalDate datoInntektsmelding = søknad.getFørsteInntektsmeldingDag();
        var ytelse = Foreldrepenger.class.cast(søknad.getYtelse());

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
        opplysninger.add(txt("infoskriv.arbeidstaker", søker.getFnr().getFnr()));
        opplysninger.add(txt("infoskriv.ytelse"));
        opplysninger.add(txt("infoskriv.startdato", formattertDato(søknad.getFørsteUttaksdag())));
        y -= renderer.addLinesOfRegularText(opplysninger, cos, y);
        y -= addBlankLine();

        List<LukketPeriodeMedVedlegg> perioder = sorted(ytelse.getFordeling().getPerioder());
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
            List<Arbeidsforhold> arbeidsforhold, FontAwareCos cos,
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

    private List<String> arbeidsgivere(List<Arbeidsforhold> arbeidsforhold, List<String> virksomhetsnummer) {
        return safeStream(arbeidsforhold)
                .filter(a -> virksomhetsnummer.contains(a.getArbeidsgiverId()))
                .map(Arbeidsforhold::getArbeidsgiverNavn)
                .map(s -> txt("arbeidsgiver", s))
                .collect(Collectors.toList());
    }

    private float renderFerieArbeidsperioder(List<UtsettelsesPeriode> ferieArbeidsperioder,
            List<Arbeidsforhold> arbeidsforhold,
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

    private float header(FontAwarePDDocument doc, FontAwareCos cos, float y) throws IOException {
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

    private float addTinyBlankLine() {
        return 10;
    }

    private float addBlankLine() {
        return 20;
    }

    private static String prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .map(p -> p.intValue() + " %")
                .orElse("Ukjent");
    }

    // gir NPE når det ikke finnes inntektsmeldingsdato.
    // fikser når vi vet hvorfor denne er null - mulig bruker ikke skal ha infoskriv
    private String fristTekstFra(LocalDate dato) {
        return erSperreFristPassert(dato) ? "" : " etter " + formattertDato(dato);
    }

    private boolean erSperreFristPassert(LocalDate fristDato) {
        return fristDato.isBefore(LocalDate.now().plusDays(1));
    }

    private List<GradertUttaksPeriode> tilGradertePerioder(List<LukketPeriodeMedVedlegg> perioder) {
        return perioder.stream()
                .filter(this::isGradertPeriode)
                .map(GradertUttaksPeriode.class::cast)
                .filter(GradertUttaksPeriode::isErArbeidstaker)
                .collect(Collectors.toList());
    }

    private boolean isGradertPeriode(LukketPeriodeMedVedlegg periode) {
        return periode instanceof GradertUttaksPeriode;
    }

    private List<UtsettelsesPeriode> ferieOgArbeid(List<LukketPeriodeMedVedlegg> periode) {
        return periode.stream()
                .filter(this::isFerieOrArbeid)
                .map(UtsettelsesPeriode.class::cast)
                .collect(Collectors.toList());
    }

    private boolean isFerieOrArbeid(LukketPeriodeMedVedlegg periode) {
        if (periode instanceof UtsettelsesPeriode) {
            UtsettelsesÅrsak årsak = UtsettelsesPeriode.class.cast(periode).getÅrsak();
            return årsak.equals(LOVBESTEMT_FERIE) || årsak.equals(ARBEID);
        }
        return false;
    }

    private static List<LukketPeriodeMedVedlegg> sorted(List<LukketPeriodeMedVedlegg> perioder) {
        Collections.sort(perioder,
                (o1, o2) -> o1.getFom().compareTo(o2.getFom()));
        return perioder;
    }

    private FontAwareCos førstesideInfoskriv(FontAwarePDDocument doc, FontAwareCos cos, PDPage scratch,
            FontAwareCos scratchcos) throws IOException {
        cos.close();
        doc.addPage(scratch);
        cos = scratchcos;
        return cos;
    }

    private FontAwareCos førstesideInfoskriv(FontAwarePDDocument doc, FontAwareCos cos) throws IOException {
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
