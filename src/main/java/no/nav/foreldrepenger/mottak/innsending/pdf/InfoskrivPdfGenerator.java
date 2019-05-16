package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.ARBEID;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.LOVBESTEMT_FERIE;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
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
    private static final String NAV_URL = "nav.no/inntektsmelding";
    private static final float STARTY = PDFElementRenderer.calculateStartY();

    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;
    private final Oppslag oppslag;

    @Inject
    public InfoskrivPdfGenerator(PDFElementRenderer renderer, SøknadTextFormatter textFormatter, Oppslag oppslag) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
        this.oppslag = oppslag;
    }

    public byte[] generate(Søknad søknad, Person søker, Kvittering kvittering) {
        try (FontAwarePDDocument doc = new FontAwarePDDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Foreldrepenger ytelse = Foreldrepenger.class.cast(søknad.getYtelse());
            List<Arbeidsforhold> arbeidsforhold = oppslag.getArbeidsforhold();
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

            if (kanSendeInntektsmelding(datoInntektsmelding)) {
                y -= renderer.addLineOfRegularText(
                    txt("infoskriv.paragraf1.passert", navn), cos, y);
            } else {
                y -= renderer.addLineOfRegularText(
                    txt("infoskriv.paragraf1.passert", navn, FMT.format(datoInntektsmelding)), cos, y);
            }

            y -= addTinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf2", NAV_URL), cos, y);
            y -= addTinyBlankLine();
            y -= renderer.addLineOfRegularText(txt("infoskriv.paragraf3", fornavn(navn)), cos, y);
            y -= addTinyBlankLine();
            y -= addBlankLine();
            y -= renderer.addLeftHeading(txt("infoskriv.opplysningerfrasøknad", navn), cos, y);
            y -= addTinyBlankLine();
            List<String> opplysninger = new ArrayList<>();
            opplysninger.add(txt("infoskriv.arbeidstaker", søker.getFnr().getFnr()));
            opplysninger.add(txt("infoskriv.ytelse"));
            opplysninger.add(txt("infoskriv.startdato", FMT.format(kvittering.getFørsteDag())));
            y -= renderer.addLinesOfRegularText(opplysninger, cos, y);
            y -= addBlankLine();
            List<LukketPeriodeMedVedlegg> perioder = sorted(ytelse.getFordeling().getPerioder());
            List<UtsettelsesPeriode> ferieArbeidsperioder = ferieOgArbeid(perioder);

            if (!ferieArbeidsperioder.isEmpty()) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float x = renderFerieArbeidsperioder(ferieArbeidsperioder, scratchcos, STARTY);
                float behov = STARTY - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderFerieArbeidsperioder(ferieArbeidsperioder, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos);
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
                    cos = nySide(doc, cos, scratch1, scratchcos);
                    y = STARTY - behov;
                }
            }

            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            LOG.warn("Generering av infoskrivPdf feilet, produserer ikke infoskriv til arbeidsgiver", e);
            return null;
        }
    }

    private float renderGradertePerioder(List<GradertUttaksPeriode> gradertePerioder,
                                         List<Arbeidsforhold> arbeidsforhold, FontAwareCos cos,
                                         float y) throws IOException {
        y -= renderer.addLineOfRegularText(txt("svp.kombinertarbeid"), cos, y);
        y -= addTinyBlankLine();
        for (GradertUttaksPeriode periode : gradertePerioder) {
            y -= renderer.addLineOfRegularText(txt("fom", FMT.format(periode.getFom())), cos, y);
            y -= renderer.addLineOfRegularText(txt("tom", FMT.format(periode.getTom())), cos, y);
            y -= renderer.addLinesOfRegularText(arbeidsgivere(arbeidsforhold, periode.getVirksomhetsnummer()), cos, y);
            y -= renderer.addLineOfRegularText(txt("arbeidstidprosent",
                prosentFra(periode.getArbeidstidProsent())), cos, y);
            y -= addTinyBlankLine();
        }
        return y;
    }

    private List<String> arbeidsgivere(List<Arbeidsforhold> arbeidsforhold, List<String> virksomhetsnummer) {
        return arbeidsforhold.stream()
            .filter(a -> virksomhetsnummer.contains(a.getArbeidsgiverId()))
            .map(Arbeidsforhold::getArbeidsgiverNavn)
            .map(s -> txt("arbeidsgiver", s))
            .collect(Collectors.toList());
    }

    private float renderFerieArbeidsperioder(List<UtsettelsesPeriode> ferieArbeidsperioder,
                                             FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLineOfRegularText(txt("svp.utsettelse"), cos, y);
        y -= addTinyBlankLine();
        for (UtsettelsesPeriode periode : ferieArbeidsperioder) {
            y -= renderer.addLineOfRegularText(txt("fom", FMT.format(periode.getFom())), cos, y);
            y -= renderer.addLineOfRegularText(txt("tom", FMT.format(periode.getTom())), cos, y);
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
    private String tilFristTekst(LocalDate dato) {
        return kanSendeInntektsmelding(dato) ? "" : " etter " + FMT.format(dato);
    }

    private boolean kanSendeInntektsmelding(LocalDate dato) {
        return dato.isBefore(LocalDate.now().plusDays(1));
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

    private FontAwareCos nySide(FontAwarePDDocument doc, FontAwareCos cos, PDPage scratch,
                                FontAwareCos scratchcos) throws IOException {
        cos.close();
        doc.addPage(scratch);
        cos = scratchcos;
        return cos;
    }

    static String fornavn(String name) {
        return Optional.ofNullable(name)
            .map(String::toLowerCase)
            .map(n -> n.substring(0, n.indexOf(" ")))
            .map(n -> Character.toUpperCase(n.charAt(0)) + n.substring(1))
            .orElse("");
    }
}
