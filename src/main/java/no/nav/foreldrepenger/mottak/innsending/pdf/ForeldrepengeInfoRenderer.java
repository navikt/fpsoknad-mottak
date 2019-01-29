package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.distinct;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Joiner;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.felles.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenOpptjening;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Frilans;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.GradertUttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.MorsAktivitet;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskOrganisasjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskOrganisasjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ÅpenPeriode;

@Component
public class ForeldrepengeInfoRenderer {

    private static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPDFGenerator.class);

    private static final float STARTY = PDFElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public ForeldrepengeInfoRenderer(PDFElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    public float header(Person søker, FontAwarePDDocument doc, FontAwareCos cos, boolean endring, float y)
            throws IOException {
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(
                endring ? txt("endringsøknad_fp", FMT.format(LocalDateTime.now()))
                        : txt("søknad_fp", FMT.format(LocalDateTime.now())),
                cos, y);
        y -= renderer.addCenteredHeadings(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    public float annenForelder(AnnenForelder annenForelder, boolean erAnnenForlderInformert,
            Rettigheter rettigheter,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("omfar"), cos, y);
        if (annenForelder instanceof NorskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, norskForelder(NorskForelder.class.cast(annenForelder)), cos, y);
            y -= renderer.addLineOfRegularText(INDENT, txt("aleneomsorg") +
                    jaNei(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        }
        else if (annenForelder instanceof UtenlandskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskForelder(annenForelder), cos, y);
            y -= renderer.addLineOfRegularText(INDENT, txt("aleneomsorg") +
                    jaNei(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        }
        else {
            y -= renderer.addLineOfRegularText(INDENT, "Jeg kan ikke oppgi navnet til den andre forelderen", cos, y);
        }

        if (!(annenForelder instanceof UkjentForelder)) {
            y -= renderer.addLineOfRegularText(INDENT, txt("harrett", jaNei(rettigheter.isHarAnnenForelderRett())), cos,
                    y);
            y -= renderer.addLineOfRegularText(INDENT, txt("informert", jaNei(erAnnenForlderInformert)), cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    public float rettigheter(Rettigheter rettigheter, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("rettigheter"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("aleneomsorg") +
                jaNei(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("omsorgiperiodene") +
                jaNei(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        return y;

    }

    public float frilansOpptjening(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        if (frilans == null) {
            return y;
        }
        y = frilans(frilans, cos, y);
        return y;
    }

    float annenOpptjening(List<AnnenOpptjening> annenOpptjening, List<Vedlegg> vedlegg, FontAwareCos cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(annenOpptjening)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("annenopptjening"), cos, y);
        for (AnnenOpptjening annen : annenOpptjening) {
            y -= renderer.addLinesOfRegularText(INDENT, annen(annen), cos, y);
            y = renderVedlegg(vedlegg, annen.getVedlegg(), "vedleggannenopptjening", cos, y);
            y -= renderer.addBlankLine();
        }

        return y;
    }

    public List<String> annen(AnnenOpptjening annen) {
        ArrayList<String> attributter = new ArrayList<>();
        attributter.add(txt("type", cap(annen.getType().name())));
        addIfSet(attributter, annen.getPeriode());
        return attributter;

    }

    public float egneNæringerOpptjening(List<EgenNæring> egneNæringer, FontAwareCos cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(egneNæringer)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("egennæring"), cos, y);
        for (List<String> næring : egneNæringer(egneNæringer)) {
            y -= renderer.addLinesOfRegularText(INDENT, næring, cos, y);
            y -= renderer.addBlankLine();
        }
        return y;
    }

    public float utenlandskeArbeidsforholdOpptjening(List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold,
            List<Vedlegg> vedlegg, FontAwareCos cos,
            float y) throws IOException {
        if (CollectionUtils.isEmpty(utenlandskArbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("utenlandskarbeid"), cos, y);
        for (UtenlandskArbeidsforhold forhold : sorterUtelandske(utenlandskArbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskeArbeidsforhold(forhold), cos, y);
            y = renderVedlegg(vedlegg, forhold.getVedlegg(), "vedleggutenlandskarbeid", cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet,
            FontAwareCos cos,
            float y) throws IOException {
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addLineOfRegularText(INDENT, txt("vedlegg1"), cos, y);
        }
        for (String id : vedleggRefs) {
            Optional<Vedlegg> details = vedlegg.stream().filter(s -> id.equals(s.getId())).findFirst();
            if (details.isPresent()) {
                String beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details.get());
                y -= renderer.addBulletPoint(INDENT,
                        txt("vedlegg2", beskrivelse, cap(details.get().getInnsendingsType().name())),
                        cos, y);
            }
            else {
                // Never, hopefully
                y -= renderer.addBulletPoint(INDENT, txt("vedlegg2", "vedlegg"), cos, y);
            }
        }
        return y;
    }

    public float arbeidsforholdOpptjening(List<Arbeidsforhold> arbeidsforhold, FontAwareCos cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(arbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("arbeidsforhold"), cos, y);

        for (Arbeidsforhold forhold : sorterArbeidsforhold(arbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(INDENT, arbeidsforhold(forhold), cos, y);
            y -= renderer.addBlankLine();
        }
        return y;
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private static List<Arbeidsforhold> sorterArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        Collections.sort(arbeidsforhold, (o1, o2) -> {
            if (o1.getFrom() != null && o2.getFrom() != null) {
                return o1.getFrom().compareTo(o2.getFrom());
            }
            return 0;
        });
        return arbeidsforhold;
    }

    private static List<UtenlandskArbeidsforhold> sorterUtelandske(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        Collections.sort(arbeidsforhold, (o1, o2) -> {
            if (o1.getPeriode() != null && o2.getPeriode() != null
                    && o1.getPeriode().getFom() != null
                    && o2.getPeriode().getFom() != null) {
                return o1.getPeriode().getFom().compareTo(o2.getPeriode().getFom());
            }
            return 0;
        });
        return arbeidsforhold;
    }

    public float frilans(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
        List<String> attributter = new ArrayList<>();
        if (frilans.getPeriode().getTom() == null) {
            addIfSet(attributter, "frilanspågår", textFormatter.dato(frilans.getPeriode().getFom()));
        }
        else {
            attributter.add(txt("frilansavsluttet", frilans.getPeriode().getFom(),
                    textFormatter.dato(frilans.getPeriode().getTom())));
        }
        attributter.add(txt("fosterhjem", jaNei(frilans.isHarInntektFraFosterhjem())));
        attributter.add(txt("nyoppstartet", jaNei(frilans.isNyOppstartet())));

        y -= renderer.addLinesOfRegularText(INDENT, attributter, cos, y);
        if (!frilans.getFrilansOppdrag().isEmpty()) {
            y -= renderer.addLineOfRegularText(INDENT, txt("oppdrag"), cos, y);
            List<String> oppdrag = frilans.getFrilansOppdrag().stream()
                    .map(o -> o.getOppdragsgiver() + " " + textFormatter.periode(o.getPeriode()))
                    .collect(toList());
            y -= renderer.addBulletList(INDENT, oppdrag, cos, y);
            y -= renderer.addBlankLine();
        }
        else {
            y -= renderer.addLineOfRegularText(INDENT, txt("oppdrag") + ": Nei", cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    private void addIfTrue(List<String> attributter, String key, boolean value) {
        if (value) {
            attributter.add(txt(key, jaNei(value)));
        }
    }

    public float medlemsskap(Medlemsskap medlemsskap, RelasjonTilBarnMedVedlegg relasjonTilBarn,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("medlemsskap"), cos, y);
        TidligereOppholdsInformasjon tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        FramtidigOppholdsInformasjon framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();
        String land = framtidigeOpphold.isFødselNorge() ? "Norge" : "utlandet";
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            y -= renderer.addLineOfRegularText(INDENT,
                    txt("terminføderi", land, pluralize(relasjonTilBarn.getAntallBarn())), cos, y);
        }
        if (relasjonTilBarn instanceof Fødsel) {
            y -= renderer.addLineOfRegularText(INDENT,
                    txt("fødtei", land, pluralize(relasjonTilBarn.getAntallBarn())), cos, y);
        }

        if (relasjonTilBarn instanceof Adopsjon) {
            if (Adopsjon.class.cast(relasjonTilBarn).getOmsorgsovertakelsesdato().isBefore(LocalDate.now())) {
                y -= renderer.addLineOfRegularText(INDENT, txt("adopsjonomsorgovertok", land), cos, y);
            }
            else {
                y -= renderer.addLineOfRegularText(INDENT, txt("adopsjonomsorgovertar", land), cos, y);
            }
        }

        if (relasjonTilBarn instanceof Omsorgsovertakelse) {
            if (Omsorgsovertakelse.class.cast(relasjonTilBarn).getOmsorgsovertakelsesdato().isBefore(LocalDate.now())) {
                y -= renderer.addLineOfRegularText(INDENT, txt("adopsjonomsorgovertok", land), cos, y);
            }
            else {
                y -= renderer.addLineOfRegularText(INDENT, txt("adopsjonomsorgovertar", land), cos, y);
            }
        }

        y -= renderer.addLineOfRegularText(INDENT, txt("siste12") + " " +
                (tidligereOpphold.isBoddINorge() ? "Norge" : ""), cos, y);
        if (!tidligereOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(INDENT, textFormatter.utenlandsOpphold(tidligereOpphold.getUtenlandsOpphold()),
                    cos, y);
        }
        y -= renderer.addLineOfRegularText(INDENT, txt("neste12") + " " +
                (framtidigeOpphold.isNorgeNeste12() ? "Norge" : ""), cos, y);

        if (!framtidigeOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(INDENT, textFormatter.utenlandsOpphold(framtidigeOpphold.getUtenlandsOpphold()),
                    cos,
                    y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    private static String pluralize(int antallBarn) {
        return antallBarn > 1 ? "a" : "et";
    }

    public float omBarn(RelasjonTilBarnMedVedlegg relasjon, FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addLeftHeading(txt("barn"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, barn(relasjon), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("antallbarn", relasjon.getAntallBarn()), cos, y);
        return y;
    }

    public FontAwareCos fordeling(FontAwarePDDocument doc, Person søker, Fordeling fordeling, Dekningsgrad dekningsgrad,
            List<Vedlegg> vedlegg,
            int antallBarn,
            boolean erEndring, FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addLeftHeading(txt("perioder"), cos, y);
        if (dekningsgrad != null) {
            y -= renderer.addLineOfRegularText(txt("dekningsgrad", dekningsgrad.kode()), cos, y);
            LOG.trace("Render dekningsgrad end at " + y);

        }
        float heaaderSize = 190;
        for (LukketPeriodeMedVedlegg periode : sorted(fordeling.getPerioder())) {
            if (periode.getClass().equals(UttaksPeriode.class)) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float x = renderUttaksPeriode(UttaksPeriode.class.cast(periode), vedlegg, antallBarn,
                        scratchcos, STARTY - 190);
                float behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderUttaksPeriode(UttaksPeriode.class.cast(periode), vedlegg, antallBarn, cos,
                            y);
                }
                else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (heaaderSize + behov);
                }
            }
            else if (periode instanceof GradertUttaksPeriode) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float x = renderGradertPeriode(GradertUttaksPeriode.class.cast(periode), vedlegg, antallBarn,
                        scratchcos,
                        STARTY - 190);
                float behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderGradertPeriode(GradertUttaksPeriode.class.cast(periode), vedlegg, antallBarn, cos,
                            y);
                }
                else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (heaaderSize + behov);
                }

            }
            else if (periode instanceof OppholdsPeriode) {
                LOG.trace("Render opphold start at " + y);
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float x = renderOppholdsPeriode(OppholdsPeriode.class.cast(periode), vedlegg, antallBarn, scratchcos,
                        STARTY - 190);
                float behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderOppholdsPeriode(OppholdsPeriode.class.cast(periode), vedlegg, antallBarn, cos,
                            y);
                }
                else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (heaaderSize + behov);
                }

            }
            else if (periode instanceof UtsettelsesPeriode) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float x = renderUtsettelsesPeriode(UtsettelsesPeriode.class.cast(periode), vedlegg, antallBarn,
                        scratchcos, STARTY - 190);
                float behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderUtsettelsesPeriode(UtsettelsesPeriode.class.cast(periode), vedlegg, antallBarn, cos,
                            y);
                }
                else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (heaaderSize + behov);
                }

            }
            else if (periode instanceof OverføringsPeriode) {
                PDPage scratch1 = newPage();
                FontAwareCos scratchcos = new FontAwareCos(doc, scratch1);
                float x = renderOverføringsPeriode(OverføringsPeriode.class.cast(periode), vedlegg, antallBarn,
                        scratchcos, STARTY - 190);
                float behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderOverføringsPeriode(OverføringsPeriode.class.cast(periode), vedlegg, antallBarn, cos,
                            y);
                }
                else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (heaaderSize + behov);
                }
            }
        }
        y -= renderer.addBlankLine();
        return cos;
    }

    private FontAwareCos nySide(FontAwarePDDocument doc, FontAwareCos cos, PDPage scratch,
            FontAwareCos scratchcos, Person søker, boolean erEndring) throws IOException {
        cos.close();
        float y = header(søker, doc, scratchcos, erEndring, STARTY);
        doc.addPage(scratch);
        cos = scratchcos;
        return cos;
    }

    public float renderOverføringsPeriode(OverføringsPeriode overføring, List<Vedlegg> vedlegg, int antallBarn,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addBulletPoint(txt("overføring"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(overføring), cos, y);
        if (!vedlegg.isEmpty()) {
            LOG.debug("Overføringsperiode har {} vedlegg med type(r) {}", vedlegg.size(),
                    vedlegg.stream().map(v -> v.getInnsendingsType()).collect(toList()));
        }
        else {
            LOG.debug("Oveføringsperiode har ingen vedlegg");
        }
        y = renderVedlegg(vedlegg, overføring.getVedlegg(), "dokumentasjon", cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    private List<String> uttaksData(OverføringsPeriode overføring) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", overføring.getFom());
        addIfSet(attributter, "tom", overføring.getTom());
        addIfSet(attributter, "dager", String.valueOf(overføring.dager()));
        attributter.add(txt("uttaksperiodetype", cap(overføring.getUttaksperiodeType().name())));
        attributter.add(txt("overføringsårsak", cap(overføring.getÅrsak().name())));
        return attributter;
    }

    public float renderUtsettelsesPeriode(UtsettelsesPeriode utsettelse, List<Vedlegg> vedlegg, int antallBarn,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addBulletPoint(txt("utsettelse"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(utsettelse), cos, y);
        if (!vedlegg.isEmpty()) {
            LOG.debug("UtsettelsesPeriode har {} vedlegg med type(r) {}", vedlegg.size(),
                    vedlegg.stream().map(v -> v.getInnsendingsType()).collect(toList()));
        }
        else {
            LOG.debug("UtsettelsesPeriode har ingen vedlegg");
        }
        y = renderVedlegg(vedlegg, utsettelse.getVedlegg(), "dokumentasjon", cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    private List<String> uttaksData(UtsettelsesPeriode utsettelse) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", utsettelse.getFom());
        addIfSet(attributter, "tom", utsettelse.getTom());
        addIfSet(attributter, "dager", String.valueOf(utsettelse.dager()));

        if (utsettelse.getUttaksperiodeType() != null) {
            attributter.add(txt("uttaksperiodetype", cap(utsettelse.getUttaksperiodeType().name())));
        }
        attributter.add(txt("utsettelsesårsak", cap(utsettelse.getÅrsak().name())));
        addIfSet(attributter, utsettelse.getMorsAktivitetsType());
        addListIfSet(attributter, "virksomhetsnummer", utsettelse.getVirksomhetsnummer());
        attributter.add(txt("erarbeidstaker", jaNei(utsettelse.isErArbeidstaker())));
        return attributter;
    }

    public float renderOppholdsPeriode(OppholdsPeriode opphold, List<Vedlegg> vedlegg, int antallBarn,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addBulletPoint(txt("opphold"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(opphold, antallBarn), cos, y);
        if (!vedlegg.isEmpty()) {
            LOG.debug("OppholdsPeriode har {} vedlegg med type(r) {}", vedlegg.size(),
                    vedlegg.stream().map(v -> v.getInnsendingsType()).collect(toList()));
        }
        else {
            LOG.debug("OppholdsPeriode har ingen vedlegg");
        }
        y = renderVedlegg(vedlegg, opphold.getVedlegg(), "dokumentasjon", cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    private List<String> uttaksData(OppholdsPeriode opphold, int antallBarn) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", opphold.getFom());
        addIfSet(attributter, "tom", opphold.getTom());
        addIfSet(attributter, "dager", String.valueOf(opphold.dager()));
        if (opphold.getÅrsak().key != null) {
            attributter.add(txt("oppholdsårsak", txt(opphold.getÅrsak().key)));
        }
        else {
            attributter.add(txt("oppholdsårsak", cap(opphold.getÅrsak().name())));
        }
        return attributter;
    }

    public float renderUttaksPeriode(UttaksPeriode uttak, List<Vedlegg> vedlegg, int antallBarn,
            FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addBulletPoint(txt("uttak"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(uttak, antallBarn), cos, y);
        if (!vedlegg.isEmpty()) {
            LOG.debug("UttaksPeriode har {} vedlegg med type(r) {}", vedlegg.size(),
                    vedlegg.stream().map(v -> v.getInnsendingsType()).collect(toList()));
        }
        else {
            LOG.debug("UttaksPeriode har ingen vedlegg");
        }
        y = renderVedlegg(vedlegg, uttak.getVedlegg(), "dokumentasjon", cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    public float renderGradertPeriode(GradertUttaksPeriode gradert, List<Vedlegg> vedlegg, int antallBarn,
            FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addBulletPoint(txt("gradertuttak"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(gradert, antallBarn), cos, y);
        if (!vedlegg.isEmpty()) {
            LOG.debug("GradertUttaksPeriode har {} vedlegg med type(r) {}", vedlegg.size(),
                    vedlegg.stream().map(v -> v.getInnsendingsType()).collect(toList()));
        }
        else {
            LOG.debug("GradertUttaksPeriode har ingen vedlegg");
        }
        y = renderVedlegg(vedlegg, gradert.getVedlegg(), "dokumentasjon", cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    private List<String> uttaksData(GradertUttaksPeriode gradert, int antallBarn) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", gradert.getFom());
        addIfSet(attributter, "tom", gradert.getTom());
        addIfSet(attributter, "dager", String.valueOf(gradert.dager()));
        attributter.add(txt("uttaksperiodetype", cap(gradert.getUttaksperiodeType().name())));
        addListIfSet(attributter, "arbeidsgiver", gradert.getVirksomhetsnummer());
        attributter.add(txt("skalgraderes", jaNei(gradert.isArbeidsForholdSomskalGraderes())));
        attributter.add(txt("erarbeidstaker", jaNei(gradert.isErArbeidstaker())));
        addIfSet(attributter, gradert.getMorsAktivitetsType());
        if (antallBarn > 1) {
            attributter.add(txt("ønskerflerbarnsdager", jaNei(gradert.isØnskerFlerbarnsdager())));
        }
        attributter.add(txt("gradertprosent", gradert.getArbeidstidProsent()));
        attributter.add(txt("ønskersamtidiguttak", jaNei(gradert.isØnskerSamtidigUttak())));
        addIfSet(attributter, gradert.isØnskerSamtidigUttak(), "samtidiguttakprosent",
                String.valueOf(gradert.getSamtidigUttakProsent()));
        return attributter;
    }

    private List<String> uttaksData(UttaksPeriode uttak, int antallBarn) {
        ArrayList<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", uttak.getFom());
        addIfSet(attributter, "tom", uttak.getTom());
        addIfSet(attributter, "dager", String.valueOf(uttak.dager()));
        attributter.add(txt("uttaksperiodetype", cap(uttak.getUttaksperiodeType().name())));
        addIfSet(attributter, uttak.getMorsAktivitetsType());
        if (antallBarn > 1) {
            attributter.add(txt("ønskerflerbarnsdager", jaNei(uttak.isØnskerFlerbarnsdager())));
        }
        attributter.add(txt("ønskersamtidiguttak", jaNei(uttak.isØnskerSamtidigUttak())));
        addIfSet(attributter, uttak.isØnskerSamtidigUttak(), "samtidiguttakprosent",
                String.valueOf(uttak.getSamtidigUttakProsent()));
        return attributter;
    }

    private static List<LukketPeriodeMedVedlegg> sorted(List<LukketPeriodeMedVedlegg> perioder) {
        Collections.sort(perioder,
                (o1, o2) -> o1.getFom().compareTo(o2.getFom()));
        return perioder;
    }

    private void addIfSet(List<String> attributter, MorsAktivitet morsAktivitetsType) {
        if (morsAktivitetsType != null) {
            attributter.add(txt("morsaktivitet", cap(morsAktivitetsType.name())));
        }

    }

    private String cap(String name) {
        return textFormatter.capitalize(name);
    }

    public float relasjonTilBarn(RelasjonTilBarnMedVedlegg relasjon, List<Vedlegg> vedlegg, FontAwareCos cos,
            float y)
            throws IOException {
        y -= renderer.addBlankLine();
        y = omBarn(relasjon, cos, y);
        y = renderVedlegg(vedlegg, relasjon.getVedlegg(), "vedleggrelasjondok", cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    public float vedlegg(List<Vedlegg> vedlegg, FontAwareCos cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(txt("vedlegg"), cos, y);
        List<String> formatted = vedlegg.stream()
                .map(Vedlegg::getBeskrivelse)
                .collect(toList());
        y -= renderer.addBulletList(formatted, cos, y);
        return startY - y;
    }

    private List<String> søker(Person søker) {
        return asList(søker.fnr.getFnr(), textFormatter.navn(søker));
    }

    private List<String> utenlandskForelder(AnnenForelder annenForelder) {
        UtenlandskForelder utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        List<String> attributter = new ArrayList<>();
        attributter.add(Optional.ofNullable(utenlandsForelder.getNavn())
                .map(n -> txt("navn", n))
                .orElse("Ukjent"));
        attributter.add(txt("nasjonalitet",
                textFormatter.countryName(utenlandsForelder.getLand(),
                        utenlandsForelder.getLand().getName())));
        addIfSet(attributter, "utenlandskid", utenlandsForelder.getId());
        return attributter;
    }

    private List<String> norskForelder(NorskForelder norskForelder) {
        return asList(
                Optional.ofNullable(norskForelder.getNavn())
                        .map(n -> txt("navn", n))
                        .orElse("Ukjent"),
                txt("fnr", norskForelder.getFnr().getFnr()));
    }

    private List<String> arbeidsforhold(Arbeidsforhold arbeidsforhold) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "arbeidsgiver", arbeidsforhold.getArbeidsgiverNavn());
        addIfSet(attributter, "fom", arbeidsforhold.getFrom());
        addIfSet(attributter, "tom", arbeidsforhold.getTo());
        if (arbeidsforhold.getStillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", arbeidsforhold.getStillingsprosent()));
        }
        return attributter;
    }

    private List<String> utenlandskeArbeidsforhold(UtenlandskArbeidsforhold ua) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "arbeidsgiver", ua.getArbeidsgiverNavn());
        addIfSet(attributter, "fom", ua.getPeriode().getFom());
        addIfSet(attributter, "tom", ua.getPeriode().getTom());
        addIfSet(attributter, "virksomhetsland", ua.getLand());
        return attributter;
    }

    private List<List<String>> egneNæringer(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(this::egenNæring)
                .collect(toList());
    }

    private List<String> egenNæring(EgenNæring næring) {
        List<String> attributter = new ArrayList<>();
        if (næring instanceof NorskOrganisasjon) {
            NorskOrganisasjon org = NorskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "virksomhetsnavn", org.getOrgName());
            addIfSet(attributter, "orgnummer", org.getOrgNummer());
            addIfSet(attributter, "registrertiland", CountryCode.NO);

        }
        if (næring instanceof UtenlandskOrganisasjon) {
            UtenlandskOrganisasjon org = UtenlandskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "virksomhetsnavn", org.getOrgName());
            addIfSet(attributter, "registrertiland", org.getRegistrertILand());
        }
        attributter.add(txt("egennæringtyper", næring.getVedlegg().size() > 1 ? "r" : "",
                næring.getVirksomhetsTyper().stream()
                        .map(v -> textFormatter.capitalize(v.toString()))
                        .collect(joining(","))));
        if (næring.getPeriode().getTom() == null) {
            addIfSet(attributter, "egennæringpågår", textFormatter.dato(næring.getPeriode().getFom()));
        }
        else {
            attributter.add(txt("egennæringavsluttet", næring.getPeriode().getFom(),
                    textFormatter.dato(næring.getPeriode().getTom())));
        }
        if (næring.getStillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", næring.getStillingsprosent()));
        }
        attributter.add(txt("nyopprettet", jaNei(næring.isErNyOpprettet())));
        attributter.add(txt("varigendring", jaNei(næring.isErVarigEndring())));
        addIfSet(attributter, "egennæringbeskrivelseendring", næring.getBeskrivelseEndring());
        addIfSet(attributter, "egennæringendringsdato", næring.getEndringsDato());
        addMoneyIfSet(attributter, "egennæringbruttoinntekt", næring.getNæringsinntektBrutto());
        if (næring.isErNyIArbeidslivet()) {
            attributter.add(txt("nyiarbeidslivet", jaNei(true)));
            addIfSet(attributter, "egennæringoppstartsdato", næring.getOppstartsDato());
        }
        Regnskapsfører rf = regnskapsfører(næring);
        if (rf != null) {
            if (rf.getTelefon() != null) {
                attributter.add(
                        txt("regnskapsførertelefon", rf.getNavn(), rf.getTelefon(), jaNei(næring.isNærRelasjon())));
            }
            else {
                attributter.add(txt("regnskapsfører", rf.getNavn(), jaNei(næring.isNærRelasjon())));
            }
        }

        return attributter;
    }

    private static Regnskapsfører regnskapsfører(EgenNæring næring) {
        if (næring == null || CollectionUtils.isEmpty(næring.getRegnskapsførere())) {
            return null;
        }
        return næring.getRegnskapsførere().get(0);
    }

    private void addIfSet(List<String> attributter, String key, String value) {
        if (value != null) {
            attributter.add(txt(key, value));
        }
    }

    private void addListIfSet(List<String> attributter, String key, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        addIfSet(attributter, key, Joiner.on(",").join(values));
    }

    private void addIfSet(List<String> attributter, boolean value, String key, String otherValue) {
        if (value) {
            attributter.add(txt(key, otherValue));
        }
    }

    private void addIfSet(List<String> attributter, String key, LocalDate dato) {
        if (dato != null) {
            attributter.add(txt(key, textFormatter.dato(dato)));
        }
    }

    private void addIfSet(List<String> attributter, String key, List<LocalDate> datoer) {
        if (!CollectionUtils.isEmpty(datoer)) {
            attributter.add(txt(key, textFormatter.datoer(datoer)));
        }
    }

    private void addIfSet(List<String> attributter, String key, Optional<LocalDate> dato) {
        if (dato.isPresent()) {
            attributter.add(txt(key, textFormatter.dato(dato.get())));
        }
    }

    private void addIfSet(List<String> attributter, ÅpenPeriode periode) {
        if (periode != null) {
            addIfSet(attributter, "fom", periode.getFom());
            addIfSet(attributter, "tom", periode.getTom());
        }
    }

    private void addMoneyIfSet(List<String> attributter, String key, Long sum) {
        if (sum != null && sum > 0L) {
            attributter.add(txt(key, String.valueOf(sum)));
        }
    }

    private String jaNei(boolean value) {
        return textFormatter.yesNo(value);
    }

    private void addIfSet(List<String> attributter, String key, CountryCode land) {
        if (land != null) {
            attributter.add(txt(key, textFormatter.countryName(land)));
        }
    }

    private List<String> barn(RelasjonTilBarnMedVedlegg relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel) {
            return fødsel(Fødsel.class.cast(relasjonTilBarn));
        }
        if (relasjonTilBarn instanceof Adopsjon) {
            return adopsjon(Adopsjon.class.cast(relasjonTilBarn));

        }
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            return termin(FremtidigFødsel.class.cast(relasjonTilBarn));
        }
        if (relasjonTilBarn instanceof Omsorgsovertakelse) {
            return omsorgsovertakelse(Omsorgsovertakelse.class.cast(relasjonTilBarn));
        }
        throw new IllegalArgumentException(relasjonTilBarn.getClass().getSimpleName() + " ikke støttet");
    }

    private List<String> termin(FremtidigFødsel termin) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fødselmedtermin", termin.getTerminDato());
        addIfSet(attributter, "utstedtdato", termin.getUtstedtDato());
        return attributter;
    }

    private List<String> adopsjon(Adopsjon adopsjon) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "adopsjonsdato", adopsjon.getOmsorgsovertakelsesdato());
        addIfSet(attributter, "ankomstdato", adopsjon.getAnkomstDato());
        addIfSet(attributter, "fødselsdato", adopsjon.getFødselsdato());
        addIfTrue(attributter, "ektefellesbarn", adopsjon.isEktefellesBarn());
        return attributter;
    }

    private List<String> fødsel(Fødsel fødsel) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fødselsdato", distinct(fødsel.getFødselsdato()));
        return attributter;
    }

    private List<String> omsorgsovertakelse(Omsorgsovertakelse overtakelse) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "omsorgsovertakelsesårsak", cap(overtakelse.getÅrsak().name()));
        addIfSet(attributter, "omsorgsovertakelsesdato", overtakelse.getOmsorgsovertakelsesdato());
        addIfSet(attributter, "omsorgsovertagelsebeskrivelse", overtakelse.getBeskrivelse());
        addIfSet(attributter, "fødselsdato", overtakelse.getFødselsdato());
        return attributter;
    }

    private String vedleggsBeskrivelse(String key, Vedlegg vedlegg) {
        return erAnnenDokumentType(vedlegg) ? txt(key) : vedlegg.getBeskrivelse();
    }

    private static boolean erAnnenDokumentType(Vedlegg vedlegg) {
        return vedlegg.getDokumentType().equals(I000060);
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

}
