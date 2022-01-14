package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.common.domain.BrukerRolle.MEDMOR;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000049;
import static no.nav.foreldrepenger.common.domain.felles.DokumentType.I000060;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.LOVBESTEMT_FERIE;
import static no.nav.foreldrepenger.common.util.StreamUtil.distinct;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.common.domain.felles.opptjening.AnnenOpptjening;
import no.nav.foreldrepenger.common.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Frilans;
import no.nav.foreldrepenger.common.domain.felles.opptjening.NorskOrganisasjon;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.common.domain.felles.opptjening.UtenlandskOrganisasjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.common.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Rettigheter;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.OppholdsPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.OverføringsPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UttaksPeriode;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@Component
public class ForeldrepengeInfoRenderer {
    private static final String UTTAKSPERIODETYPE = "uttaksperiodetype";
    private static final String FØDSELSDATO = "fødselsdato";
    private static final String DOKUMENTASJON = "dokumentasjon";
    private static final String DAGER = "dager";
    private static final String ARBEIDSGIVER = "arbeidsgiver";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final float STARTY = PdfElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public ForeldrepengeInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    public float header(Person søker, FontAwarePdfDocument doc, FontAwareCos cos, boolean endring, float y)
            throws IOException {
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(
                endring ? txt("endringsøknad_fp")
                        : txt("søknad_fp"),
                cos, y);
        y -= renderer.addCenteredRegular(
                textFormatter.fromMessageSource("mottatt", FMT.format(LocalDateTime.now())), cos, y);
        y -= renderer.addCenteredRegulars(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        return y;
    }

    public float annenForelder(AnnenForelder annenForelder, boolean erAnnenForlderInformert,
            Rettigheter rettigheter,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("omannenforelder"), cos, y);
        if (annenForelder instanceof NorskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, norskForelder(NorskForelder.class.cast(annenForelder)), cos, y);
            y -= renderer.addLineOfRegularText(INDENT,
                    txt("aleneomsorg", jaNei(rettigheter.isHarAleneOmsorgForBarnet())), cos, y);
        } else if (annenForelder instanceof UtenlandskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskForelder(annenForelder), cos, y);
            y -= renderer.addLineOfRegularText(INDENT,
                    txt("aleneomsorg", jaNei(rettigheter.isHarAleneOmsorgForBarnet())), cos, y);
        } else {
            y -= renderer.addLineOfRegularText(INDENT, "Jeg kan ikke oppgi navnet til den andre forelderen", cos, y);
        }
        if (!(annenForelder instanceof UkjentForelder)) {
            y -= renderer.addLineOfRegularText(INDENT, txt("harrett", jaNei(rettigheter.isHarAnnenForelderRett())), cos,
                    y);
            y -= renderer.addLineOfRegularText(INDENT, txt("informert", jaNei(erAnnenForlderInformert)), cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    public float rettigheter(Rettigheter rettigheter, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("rettigheter"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("aleneomsorg", jaNei(rettigheter.isHarAleneOmsorgForBarnet())),
                cos, y);
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
            y -= PdfElementRenderer.BLANK_LINE;
        }
        return y;
    }

    public List<String> annen(AnnenOpptjening annen) {
        var attributter = new ArrayList<String>();
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
            y -= PdfElementRenderer.BLANK_LINE;
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
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet,
            FontAwareCos cos,
            float y) throws IOException {
        if (!vedleggRefs.isEmpty()) {
            y -= renderer.addLineOfRegularText(INDENT, txt("vedlegg1"), cos, y);
        }
        for (String id : vedleggRefs) {
            var details = safeStream(vedlegg)
                    .filter(s -> id.equals(s.getId()))
                    .findFirst();
            if (details.isPresent()) {
                var beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details.get());
                y -= renderer.addBulletPoint(INDENT,
                        txt("vedlegg2", beskrivelse, cap(details.get().getInnsendingsType().name())),
                        cos, y);
            } else {
                // Never, hopefully
                y -= renderer.addBulletPoint(INDENT, txt("vedlegg2", "vedlegg"), cos, y);
            }
        }
        return y;
    }

    public float arbeidsforholdOpptjening(List<EnkeltArbeidsforhold> arbeidsforhold, FontAwareCos cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(arbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("arbeidsforhold"), cos, y);
        for (EnkeltArbeidsforhold forhold : sorterArbeidsforhold(arbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(INDENT, arbeidsforhold(forhold), cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        }
        return y;
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private static List<EnkeltArbeidsforhold> sorterArbeidsforhold(List<EnkeltArbeidsforhold> arbeidsforhold) {
        arbeidsforhold.sort((o1, o2) -> {
            if (o1.getFrom() != null && o2.getFrom() != null) {
                return o1.getFrom().compareTo(o2.getFrom());
            }
            return 0;
        });
        return arbeidsforhold;
    }

    private static List<UtenlandskArbeidsforhold> sorterUtelandske(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        arbeidsforhold.sort((o1, o2) -> {
            if (o1.getPeriode() != null && o2.getPeriode() != null
                && o1.getPeriode().fom() != null
                && o2.getPeriode().fom() != null) {
                return o1.getPeriode().fom().compareTo(o2.getPeriode().fom());
            }
            return 0;
        });
        return arbeidsforhold;
    }

    public float frilans(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
        List<String> attributter = new ArrayList<>();
        if (frilans.isJobberFremdelesSomFrilans()) {
            addIfSet(attributter, "frilanspågår", textFormatter.dato(frilans.getPeriode().fom()));
        } else {
            attributter.add(txt("frilansavsluttet", textFormatter.dato(frilans.getPeriode().fom())));
        }
        attributter.add(txt("fosterhjem", jaNei(frilans.isHarInntektFraFosterhjem())));
        attributter.add(txt("nyoppstartet", jaNei(frilans.isNyOppstartet())));
        y -= renderer.addLinesOfRegularText(INDENT, attributter, cos, y);
        if (!frilans.getFrilansOppdrag().isEmpty()) {
            y -= renderer.addLineOfRegularText(INDENT, txt("oppdrag"), cos, y);
            var oppdrag = safeStream(frilans.getFrilansOppdrag())
                    .map(o -> o.oppdragsgiver() + " " + textFormatter.periode(o.periode()))
                    .toList();
            y -= renderer.addBulletList(INDENT, oppdrag, cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        } else {
            y -= renderer.addLineOfRegularText(INDENT, txt("oppdrag") + ": Nei", cos, y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private void addIfTrue(List<String> attributter, String key, boolean value) {
        if (value) {
            attributter.add(txt(key, jaNei(value)));
        }
    }

    public float medlemsskap(Medlemsskap medlemsskap, RelasjonTilBarn relasjonTilBarn,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("medlemsskap"), cos, y);
        var tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        var framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();
        var land = textFormatter.countryName(medlemsskap.landVedDato(relasjonTilBarn.relasjonsDato()));
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
            } else {
                y -= renderer.addLineOfRegularText(INDENT, txt("adopsjonomsorgovertar", land), cos, y);
            }
        }
        if (relasjonTilBarn instanceof Omsorgsovertakelse) {
            if (Omsorgsovertakelse.class.cast(relasjonTilBarn).getOmsorgsovertakelsesdato().isBefore(LocalDate.now())) {
                y -= renderer.addLineOfRegularText(INDENT, txt("adopsjonomsorgovertok", land), cos, y);
            } else {
                y -= renderer.addLineOfRegularText(INDENT, txt("adopsjonomsorgovertar", land), cos, y);
            }
        }
        y -= renderer.addLineOfRegularText(INDENT, txt("siste12") +
                (tidligereOpphold.isBoddINorge() ? " Norge" : ":"), cos, y);
        if (!tidligereOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(INDENT, textFormatter.utenlandsOpphold(tidligereOpphold.getUtenlandsOpphold()),
                    cos, y);
        }
        y -= renderer.addLineOfRegularText(INDENT, txt("neste12") +
                (framtidigeOpphold.isNorgeNeste12() ? " Norge" : ":"), cos, y);
        if (!framtidigeOpphold.getUtenlandsOpphold().isEmpty()) {
            y -= renderer.addBulletList(INDENT, textFormatter.utenlandsOpphold(framtidigeOpphold.getUtenlandsOpphold()),
                    cos,
                    y);
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private static String pluralize(int antallBarn) {
        return antallBarn > 1 ? "a" : "et";
    }

    public float omBarn(RelasjonTilBarn relasjon, FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addLeftHeading(txt("barn"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, barn(relasjon), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("antallbarn", relasjon.getAntallBarn()), cos, y);
        return y;
    }

    public FontAwareCos fordeling(FontAwarePdfDocument doc, Person søker, BrukerRolle rolle, Fordeling fordeling,
            Dekningsgrad dekningsgrad,
            List<Vedlegg> vedlegg,
            int antallBarn,
            boolean erEndring, FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addLeftHeading(txt("perioder"), cos, y);
        if (dekningsgrad != null) {
            y -= renderer.addLineOfRegularText(txt("dekningsgrad", dekningsgrad.kode()), cos, y);
        }
        var headerSize = 190F;
        for (LukketPeriodeMedVedlegg periode : sorted(fordeling.getPerioder())) {
            if (periode.getClass().equals(UttaksPeriode.class)) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderUttaksPeriode(UttaksPeriode.class.cast(periode), rolle, vedlegg, antallBarn,
                        scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderUttaksPeriode(UttaksPeriode.class.cast(periode), rolle, vedlegg, antallBarn, cos,
                            y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof GradertUttaksPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderGradertPeriode(GradertUttaksPeriode.class.cast(periode), rolle, vedlegg, antallBarn,
                        scratchcos,
                        STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderGradertPeriode(GradertUttaksPeriode.class.cast(periode), rolle, vedlegg, antallBarn, cos,
                            y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof OppholdsPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderOppholdsPeriode(OppholdsPeriode.class.cast(periode), vedlegg, scratchcos,
                        STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderOppholdsPeriode(OppholdsPeriode.class.cast(periode), vedlegg, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof UtsettelsesPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderUtsettelsesPeriode(UtsettelsesPeriode.class.cast(periode), rolle, vedlegg,
                        scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderUtsettelsesPeriode(UtsettelsesPeriode.class.cast(periode), rolle, vedlegg,
                            cos,
                            y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof OverføringsPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderOverføringsPeriode(OverføringsPeriode.class.cast(periode), rolle, vedlegg,
                        scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderOverføringsPeriode(OverføringsPeriode.class.cast(periode), rolle, vedlegg,
                            cos,
                            y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, søker, erEndring);
                    y = STARTY - (headerSize + behov);
                }
            }
        }
        return cos;
    }

    public float renderTilleggsopplysninger(String tilleggsopplysninger, FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addLeftHeading(txt("tilleggsopplysninger"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, tilleggsopplysninger, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private FontAwareCos nySide(FontAwarePdfDocument doc, FontAwareCos cos, PDPage scratch,
            FontAwareCos scratchcos, Person søker, boolean erEndring) throws IOException {
        cos.close();
        header(søker, doc, scratchcos, erEndring, STARTY);
        doc.addPage(scratch);
        cos = scratchcos;
        return cos;
    }

    public float renderOverføringsPeriode(OverføringsPeriode overføring, BrukerRolle rolle, List<Vedlegg> vedlegg,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addBulletPoint(txt("overføring"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(overføring, rolle), cos, y);
        y = renderVedlegg(vedlegg, overføring.getVedlegg(), DOKUMENTASJON, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private List<String> uttaksData(OverføringsPeriode overføring, BrukerRolle rolle) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", overføring.getFom());
        addIfSet(attributter, "tom", overføring.getTom());
        addIfSet(attributter, DAGER, String.valueOf(overføring.dager()));
        attributter.add(txt(UTTAKSPERIODETYPE, kontoTypeForRolle(overføring.getUttaksperiodeType(), rolle)));
        attributter.add(txt("overføringsårsak", cap(overføring.getÅrsak().name())));
        return attributter;
    }

    public float renderUtsettelsesPeriode(UtsettelsesPeriode utsettelse, BrukerRolle rolle, List<Vedlegg> vedlegg,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addBulletPoint(txt("utsettelse"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(utsettelse, rolle), cos, y);
        y = renderVedlegg(vedlegg, utsettelse.getVedlegg(), DOKUMENTASJON, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private List<String> uttaksData(UtsettelsesPeriode utsettelse, BrukerRolle rolle) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", utsettelse.getFom());
        addIfSet(attributter, "tom", utsettelse.getTom());
        addIfSet(attributter, DAGER, String.valueOf(utsettelse.dager()));
        if (utsettelse.getUttaksperiodeType() != null) {
            attributter
                    .add(txt(UTTAKSPERIODETYPE, kontoTypeForRolle(utsettelse.getUttaksperiodeType(), rolle)));
        }

        if (utsettelse.getÅrsak() != null) {
            if (utsettelse.getÅrsak().getKey() != null) {
                attributter.add(txt("utsettelsesårsak", txt(utsettelse.getÅrsak().getKey())));
            } else {
                attributter.add(txt("utsettelsesårsak", cap(utsettelse.getÅrsak().name())));
            }
        }

        // attributter.add(txt("utsettelsesårsak", cap(utsettelse.getÅrsak().name())));
        addIfSet(attributter, utsettelse.getMorsAktivitetsType());
        addListIfSet(attributter, "virksomhetsnummer", utsettelse.getVirksomhetsnummer());
        if (!utsettelse.getÅrsak().equals(LOVBESTEMT_FERIE)) {
            attributter.add(txt("erarbeidstaker", jaNei(utsettelse.isErArbeidstaker())));
        }
        return attributter;
    }

    public float renderOppholdsPeriode(OppholdsPeriode opphold, List<Vedlegg> vedlegg,
            FontAwareCos cos, float y) throws IOException {
        y -= renderer.addBulletPoint(txt("opphold"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(opphold), cos, y);
        y = renderVedlegg(vedlegg, opphold.getVedlegg(), DOKUMENTASJON, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private List<String> uttaksData(OppholdsPeriode opphold) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", opphold.getFom());
        addIfSet(attributter, "tom", opphold.getTom());
        addIfSet(attributter, DAGER, String.valueOf(opphold.dager()));
        if (opphold.getÅrsak().getKey() != null) {
            attributter.add(txt("oppholdsårsak", txt(opphold.getÅrsak().getKey())));
        } else {
            attributter.add(txt("oppholdsårsak", cap(opphold.getÅrsak().name())));
        }
        return attributter;
    }

    public float renderUttaksPeriode(UttaksPeriode uttak, BrukerRolle rolle, List<Vedlegg> vedlegg, int antallBarn,
            FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addBulletPoint(txt("uttak"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(uttak, antallBarn, rolle), cos, y);
        y = renderVedlegg(vedlegg, uttak.getVedlegg(), DOKUMENTASJON, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    public float renderGradertPeriode(GradertUttaksPeriode gradert, BrukerRolle rolle, List<Vedlegg> vedlegg,
            int antallBarn,
            FontAwareCos cos, float y)
            throws IOException {
        y -= renderer.addBulletPoint(txt("gradertuttak"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(gradert, antallBarn, rolle), cos, y);
        y = renderVedlegg(vedlegg, gradert.getVedlegg(), DOKUMENTASJON, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private List<String> uttaksData(GradertUttaksPeriode gradert, int antallBarn, BrukerRolle rolle) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", gradert.getFom());
        addIfSet(attributter, "tom", gradert.getTom());
        addIfSet(attributter, DAGER, String.valueOf(gradert.dager()));
        attributter.add(txt(UTTAKSPERIODETYPE, kontoTypeForRolle(gradert.getUttaksperiodeType(), rolle)));
        addListIfSet(attributter, ARBEIDSGIVER, gradert.getVirksomhetsnummer());
        attributter.add(txt("skalgraderes", jaNei(gradert.isArbeidsForholdSomskalGraderes())));
        attributter.add(txt("erarbeidstaker", jaNei(gradert.isErArbeidstaker())));
        addIfSet(attributter, "erfrilans", gradert.getFrilans());
        addIfSet(attributter, "erselvstendig", gradert.getSelvstendig());
        addIfSet(attributter, gradert.getMorsAktivitetsType());
        if (antallBarn > 1) {
            attributter.add(txt("ønskerflerbarnsdager", jaNei(gradert.isØnskerFlerbarnsdager())));
        }
        attributter.add(txt("gradertprosent", prosentFra(gradert.getArbeidstidProsent())));
        attributter.add(txt("ønskersamtidiguttak", jaNei(gradert.isØnskerSamtidigUttak())));
        addIfSet(attributter, gradert.isØnskerSamtidigUttak(), "samtidiguttakprosent",
                String.valueOf(prosentFra(gradert.getSamtidigUttakProsent())));
        return attributter;
    }

    private static double prosentFra(ProsentAndel prosent) {
        return Optional.ofNullable(prosent)
                .map(ProsentAndel::getProsent)
                .orElse(0d);
    }

    private void addIfSet(List<String> attributter, String key, Boolean value) {
        if (value != null) {
            attributter.add(txt(key, jaNei(value.booleanValue())));
        }
    }

    private List<String> uttaksData(UttaksPeriode uttak, int antallBarn, BrukerRolle rolle) {
        var attributter = new ArrayList<String>();
        addIfSet(attributter, "fom", uttak.getFom());
        addIfSet(attributter, "tom", uttak.getTom());
        addIfSet(attributter, DAGER, String.valueOf(uttak.dager()));
        attributter.add(txt(UTTAKSPERIODETYPE, kontoTypeForRolle(uttak.getUttaksperiodeType(), rolle)));
        addIfSet(attributter, uttak.getMorsAktivitetsType());
        if (antallBarn > 1) {
            attributter.add(txt("ønskerflerbarnsdager", jaNei(uttak.isØnskerFlerbarnsdager())));
        }
        attributter.add(txt("ønskersamtidiguttak", jaNei(uttak.isØnskerSamtidigUttak())));
        addIfSet(attributter, uttak.isØnskerSamtidigUttak(), "samtidiguttakprosent",
                String.valueOf(prosentFra(uttak.getSamtidigUttakProsent())));
        return attributter;
    }

    private String kontoTypeForRolle(StønadskontoType type, BrukerRolle rolle) {
        if (MEDMOR.equals(rolle) && FEDREKVOTE.equals(type)) {
            return txt("uttakfedrekvotemedmor");
        }
        return cap(type.name());
    }

    private static List<LukketPeriodeMedVedlegg> sorted(List<LukketPeriodeMedVedlegg> perioder) {
        Collections.sort(perioder,
                (o1, o2) -> o1.getFom().compareTo(o2.getFom()));
        return perioder;
    }

    private void addIfSet(List<String> attributter, MorsAktivitet morsAktivitetsType) {
        if (morsAktivitetsType != null) {
            if (morsAktivitetsType.getKey() != null) {
                attributter.add(txt("morsaktivitet", txt(morsAktivitetsType.getKey())));
            } else {
                attributter.add(txt("morsaktivitet", cap(morsAktivitetsType.name())));
            }
        }
    }

    private String cap(String name) {
        return textFormatter.capitalize(name);
    }

    public float relasjonTilBarn(RelasjonTilBarn relasjon, List<Vedlegg> vedlegg, FontAwareCos cos,
            float y)
            throws IOException {
        y -= PdfElementRenderer.BLANK_LINE;
        y = omBarn(relasjon, cos, y);
        y = renderVedlegg(vedlegg, relasjon.getVedlegg(), "vedleggrelasjondok", cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    public float vedlegg(List<Vedlegg> vedlegg, FontAwareCos cos, float y) throws IOException {
        var startY = y;
        y -= renderer.addLeftHeading(txt("vedlegg"), cos, y);
        var formatted = safeStream(vedlegg)
                .map(Vedlegg::getBeskrivelse)
                .toList();
        y -= renderer.addBulletList(formatted, cos, y);
        return startY - y;
    }

    private List<String> søker(Person søker) {
        return asList(
                textFormatter.navn(new Navn(søker.getFornavn(), søker.getMellomnavn(), søker.getEtternavn())),
                textFormatter.fromMessageSource("fødselsnummerinline", søker.fnr().value()));
    }

    private List<String> utenlandskForelder(AnnenForelder annenForelder) {
        var utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        List<String> attributter = new ArrayList<>();
        attributter.add(Optional.ofNullable(utenlandsForelder.getNavn())
                .map(n -> txt("navninline", n))
                .orElse("Ukjent"));
        attributter.add(txt("nasjonalitetinline",
                textFormatter.countryName(utenlandsForelder.getLand(),
                        utenlandsForelder.getLand().getName())));
        addIfSet(attributter, "utenlandskid", utenlandsForelder.getId());
        return attributter;
    }

    private List<String> norskForelder(NorskForelder norskForelder) {
        return asList(
                Optional.ofNullable(norskForelder.getNavn())
                        .map(n -> txt("navninline", n))
                        .orElse("Ukjent"),
                txt("fnr", norskForelder.getFnr().value()));
    }

    private List<String> arbeidsforhold(EnkeltArbeidsforhold arbeidsforhold) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, ARBEIDSGIVER, arbeidsforhold.getArbeidsgiverNavn());
        addIfSet(attributter, "fom", arbeidsforhold.getFrom());
        addIfSet(attributter, "tom", arbeidsforhold.getTo());
        if (arbeidsforhold.getStillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", prosentFra(arbeidsforhold.getStillingsprosent())));
        }
        return attributter;
    }

    private List<String> utenlandskeArbeidsforhold(UtenlandskArbeidsforhold ua) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, ARBEIDSGIVER, ua.getArbeidsgiverNavn());
        addIfSet(attributter, "fom", ua.getPeriode().fom());
        addIfSet(attributter, "tom", ua.getPeriode().tom());
        addIfSet(attributter, "virksomhetsland", ua.getLand());
        return attributter;
    }

    private List<List<String>> egneNæringer(List<EgenNæring> egenNæring) {
        return safeStream(egenNæring)
                .map(this::egenNæring)
                .toList();
    }

    private List<String> egenNæring(EgenNæring næring) {
        List<String> attributter = new ArrayList<>();
        if (næring instanceof NorskOrganisasjon) {
            var org = NorskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "virksomhetsnavn", org.getOrgName());
            addIfSet(attributter, "orgnummer", org.getOrgNummer().value());
            addIfSet(attributter, "registrertiland", CountryCode.NO);
        }
        if (næring instanceof UtenlandskOrganisasjon) {
            var org = UtenlandskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "virksomhetsnavn", org.getOrgName());
            addIfSet(attributter, "registrertiland", org.getRegistrertILand());
        }
        attributter.add(txt("egennæringtyper", næring.getVedlegg().size() > 1 ? "r" : "",
                safeStream(næring.getVirksomhetsTyper())
                        .map(v -> textFormatter.capitalize(v.toString()))
                        .collect(joining(","))));
        if (næring.getPeriode().tom() == null) {
            addIfSet(attributter, "egennæringpågår", textFormatter.dato(næring.getPeriode().fom()));
        } else {
            attributter.add(txt("egennæringavsluttet", næring.getPeriode().fom(),
                    textFormatter.dato(næring.getPeriode().tom())));
        }
        if (næring.getStillingsprosent() != null) {
            attributter.add(txt("stillingsprosent", prosentFra(næring.getStillingsprosent())));
        }
        attributter.add(txt("nyligyrkesaktiv", jaNei(næring.isErNyIArbeidslivet())));
        attributter.add(txt("varigendring", jaNei(næring.isErVarigEndring())));
        addIfSet(attributter, "egennæringbeskrivelseendring", næring.getBeskrivelseEndring());
        addIfSet(attributter, "egennæringendringsdato", næring.getEndringsDato());
        if (næring.isErNyOpprettet() || næring.isErVarigEndring()) {
            addMoneyIfSet(attributter, "egennæringbruttoinntekt", næring.getNæringsinntektBrutto());
        }
        if (næring.isErNyOpprettet()) {
            attributter.add(txt("nystartetvirksomhet", jaNei(true)));
            addIfSet(attributter, "egennæringoppstartsdato", næring.getOppstartsDato());
        }
        var rf = regnskapsfører(næring);
        if (rf != null) {
            if (rf.telefon() != null) {
                attributter.add(
                        txt("regnskapsførertelefon", rf.navn(), rf.telefon(), jaNei(næring.isNærRelasjon())));
            } else {
                attributter.add(txt("regnskapsfører", rf.navn(), jaNei(næring.isNærRelasjon())));
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
        addIfSet(attributter, key, String.join(",", values));
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
            addIfSet(attributter, "fom", periode.fom());
            addIfSet(attributter, "tom", periode.tom());
        }
    }

    private void addMoneyIfSet(List<String> attributter, String key, Long sum) {
        if (sum != null) {
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

    private List<String> barn(RelasjonTilBarn relasjonTilBarn) {
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
        addIfSet(attributter, FØDSELSDATO, adopsjon.getFødselsdato());
        addIfTrue(attributter, "ektefellesbarn", adopsjon.isEktefellesBarn());
        return attributter;
    }

    private List<String> fødsel(Fødsel fødsel) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, FØDSELSDATO, distinct(fødsel.getFødselsdato()));
        addIfSet(attributter, "termindato", fødsel.getTermindato());
        return attributter;
    }

    private List<String> omsorgsovertakelse(Omsorgsovertakelse overtakelse) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "omsorgsovertakelsesårsak", cap(overtakelse.getÅrsak().name()));
        addIfSet(attributter, "omsorgsovertakelsesdato", overtakelse.getOmsorgsovertakelsesdato());
        addIfSet(attributter, "omsorgsovertagelsebeskrivelse", overtakelse.getBeskrivelse());
        addIfSet(attributter, FØDSELSDATO, overtakelse.getFødselsdato());
        return attributter;
    }

    private String vedleggsBeskrivelse(String key, Vedlegg vedlegg) {
        return erAnnenDokumentType(vedlegg) ? txt(key) : vedlegg.getBeskrivelse();
    }

    private static boolean erAnnenDokumentType(Vedlegg vedlegg) {
        return vedlegg.getDokumentType().equals(I000060) || vedlegg.getDokumentType().equals(I000049);
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [renderer=" + renderer + ", textFormatter=" + textFormatter + "]";
    }

    public void addOutlineItem(FontAwarePdfDocument doc, PDPage page, PdfOutlineItem title) {
        renderer.addOutlineItem(doc, page, title);
    }
}
