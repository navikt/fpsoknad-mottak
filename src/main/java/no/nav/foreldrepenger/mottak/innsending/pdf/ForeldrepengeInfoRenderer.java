package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.common.domain.BrukerRolle.FAR;
import static no.nav.foreldrepenger.common.domain.BrukerRolle.MEDMOR;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.LOVBESTEMT_FERIE;
import static no.nav.foreldrepenger.common.util.LangUtil.toBoolean;
import static no.nav.foreldrepenger.common.util.StreamUtil.distinct;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.common.domain.felles.opptjening.AnnenOpptjening;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.common.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Rettigheter;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.OppholdsPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.OverføringsPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UttaksPeriode;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;

@Component
public class ForeldrepengeInfoRenderer extends FellesSøknadInfoRenderer {
    private static final String UTTAKSPERIODETYPE = "uttaksperiodetype";
    private static final String FØDSELSDATO = "fødselsdato";
    private static final String DOKUMENTASJON = "dokumentasjon";
    private static final String DAGER = "dager";
    private static final String ALENESORG_KEY = "aleneomsorg";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final float STARTY = PdfElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private final PdfElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public ForeldrepengeInfoRenderer(PdfElementRenderer renderer, SøknadTextFormatter textFormatter) {
        super(renderer, textFormatter);
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    public float header(FontAwarePdfDocument doc, FontAwareCos cos, boolean endring, float y, InnsendingPersonInfo person) throws IOException {
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(
                endring ? txt("endringsøknad_fp")
                        : txt("søknad_fp"),
                cos, y);
        y -= renderer.addCenteredRegular(
                textFormatter.fromMessageSource("mottatt", FMT.format(LocalDateTime.now())), cos, y);
        y -= renderer.addCenteredRegulars(søker(person), cos, y);
        y -= renderer.addDividerLine(cos, y);
        return y;
    }

    public float annenForelder(AnnenForelder annenForelder, Boolean erAnnenForlderInformert,
            Rettigheter rettigheter, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("omannenforelder"), cos, y);
        if (annenForelder instanceof NorskForelder norskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, norskForelder(norskForelder), cos, y);
            if (rettigheter.harAleneOmsorgForBarnet() != null) {
                y -= renderer.addLineOfRegularText(INDENT,
                    txt(ALENESORG_KEY, jaNei(rettigheter.harAleneOmsorgForBarnet())), cos, y);
            }
        } else if (annenForelder instanceof UtenlandskForelder utenlandskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskForelder(utenlandskForelder), cos, y);
            if (rettigheter.harAleneOmsorgForBarnet() != null) {
                y -= renderer.addLineOfRegularText(INDENT,
                    txt(ALENESORG_KEY, jaNei(rettigheter.harAleneOmsorgForBarnet())), cos, y);
            }
        } else {
            y -= renderer.addLineOfRegularText(INDENT, "Jeg kan ikke oppgi navnet til den andre forelderen", cos, y);
        }
        if (!(annenForelder instanceof UkjentForelder)) {
            y -= renderer.addLineOfRegularText(INDENT, txt("harrett", jaNei(rettigheter.harAnnenForelderRett())), cos, y);
            y = annenForelderOppholdtSegIEøs(rettigheter, cos, y);
            y = annenForelderTilsvarendeRettEøs(rettigheter, cos, y);
            y = morUfør(rettigheter, cos, y);
            if (erAnnenForlderInformert != null) {
                y -= renderer.addLineOfRegularText(INDENT, txt("informert", jaNei(erAnnenForlderInformert)), cos, y);
            }
        }
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private float annenForelderOppholdtSegIEøs(Rettigheter rettigheter, FontAwareCos cos, float y) throws IOException {
        if (rettigheter.harAnnenForelderOppholdtSegIEØS() != null) {
            y -= renderer.addLineOfRegularText(INDENT, txt("annenForelderOppholdtSegIEos",
                jaNei(rettigheter.harAnnenForelderOppholdtSegIEØS())), cos, y);
        }
        return y;
    }

    private float annenForelderTilsvarendeRettEøs(Rettigheter rettigheter, FontAwareCos cos, float y) throws IOException {
        if (rettigheter.harAnnenForelderTilsvarendeRettEØS() != null) {
            y -= renderer.addLineOfRegularText(INDENT, txt("annenforelderTilsvarendeEosRett",
                jaNei(rettigheter.harAnnenForelderTilsvarendeRettEØS())), cos, y);
        }
        return y;
    }

    private float morUfør(Rettigheter rettigheter, FontAwareCos cos, float y) throws IOException {
        if (rettigheter.harMorUføretrygd() != null) {
            y -= renderer.addLineOfRegularText(INDENT, txt("harmorufor", jaNei(rettigheter.harMorUføretrygd())), cos, y);
        }
        return y;
    }

    float annenOpptjening(List<AnnenOpptjening> annenOpptjening, List<Vedlegg> vedlegg, FontAwareCos cos, float y) throws IOException {
        if (CollectionUtils.isEmpty(annenOpptjening)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("annenopptjening"), cos, y);
        for (var annen : annenOpptjening) {
            y -= renderer.addLinesOfRegularText(INDENT, annen(annen), cos, y);
            y = renderVedlegg(vedlegg, annen.vedlegg(), "vedleggannenopptjening", cos, y);
            y -= PdfElementRenderer.BLANK_LINE;
        }
        return y;
    }

    public List<String> annen(AnnenOpptjening annen) {
        var attributter = new ArrayList<String>();
        attributter.add(txt("type", cap(annen.type().name())));
        addIfSet(attributter, annen.periode());
        return attributter;
    }

    private static PDPage newPage() {
        return new PDPage(A4);
    }

    private void addIfTrue(List<String> attributter, String key, boolean value) {
        if (value) {
            attributter.add(txt(key, jaNei(value)));
        }
    }

    private static String pluralize(int antallBarn) {
        return antallBarn > 1 ? "a" : "et";
    }

    public float omBarn(RelasjonTilBarn relasjon, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("barn"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, barn(relasjon), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("antallbarn", relasjon.getAntallBarn()), cos, y);
        return y;
    }

    public FontAwareCos fordeling(FontAwarePdfDocument doc, BrukerRolle rolle, Foreldrepenger stønad,
                                  List<Vedlegg> vedlegg, boolean erEndring, FontAwareCos cos, float y, InnsendingPersonInfo person)
            throws IOException {
        var fordeling = stønad.fordeling();
        var dekningsgrad = stønad.dekningsgrad();
        var antallBarn = stønad.relasjonTilBarn().getAntallBarn();

        y -= renderer.addLeftHeading(txt("perioder"), cos, y);
        if (dekningsgrad != null) {
            y -= renderer.addLineOfRegularText(txt("dekningsgrad", dekningsgrad.kode()), cos, y);
        }
        var headerSize = 190F;
        for (var periode : sorted(fordeling.perioder())) {
            if (periode.getClass().equals(UttaksPeriode.class)) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderUttaksPeriode((UttaksPeriode) periode, rolle, vedlegg, antallBarn, scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderUttaksPeriode((UttaksPeriode) periode, rolle, vedlegg, antallBarn, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, erEndring, person);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof GradertUttaksPeriode gradertUttaksPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderGradertPeriode(gradertUttaksPeriode, rolle, vedlegg, antallBarn, scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderGradertPeriode(gradertUttaksPeriode, rolle, vedlegg, antallBarn, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, erEndring, person);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof OppholdsPeriode oppholdsPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderOppholdsPeriode(oppholdsPeriode, vedlegg, scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderOppholdsPeriode(oppholdsPeriode, vedlegg, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, erEndring, person);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof UtsettelsesPeriode utsettelsesPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderUtsettelsesPeriode(utsettelsesPeriode, rolle, vedlegg, scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderUtsettelsesPeriode(utsettelsesPeriode, rolle, vedlegg, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, erEndring, person);
                    y = STARTY - (headerSize + behov);
                }
            } else if (periode instanceof OverføringsPeriode overføringsPeriode) {
                var scratch1 = newPage();
                var scratchcos = new FontAwareCos(doc, scratch1);
                var x = renderOverføringsPeriode(overføringsPeriode, rolle, vedlegg, scratchcos, STARTY - 190);
                var behov = STARTY - 190 - x;
                if (behov < y) {
                    scratchcos.close();
                    y = renderOverføringsPeriode(overføringsPeriode, rolle, vedlegg, cos, y);
                } else {
                    cos = nySide(doc, cos, scratch1, scratchcos, erEndring, person);
                    y = STARTY - (headerSize + behov);
                }
            }
        }
        if (fordeling.ønskerJustertUttakVedFødsel() != null) {
            var scratch1 = newPage();
            var scratchcos = new FontAwareCos(doc, scratch1);
            var x = STARTY - 190;
            x -= renderer.addLineOfRegularText(
                txt("fp.justeruttak", jaNei(toBoolean(fordeling.ønskerJustertUttakVedFødsel())), pluralize(antallBarn)), scratchcos, STARTY - 190);
            var behov = STARTY - 190 - x;
            if (behov < y) {
                scratchcos.close();
                renderer.addLineOfRegularText(txt("fp.justeruttak", jaNei(toBoolean(fordeling.ønskerJustertUttakVedFødsel())), pluralize(antallBarn)),
                    cos, y);
            } else {
                cos = nySide(doc, cos, scratch1, scratchcos, erEndring, person);
            }
        }
        return cos;
    }

    public float renderTilleggsopplysninger(String tilleggsopplysninger, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("tilleggsopplysninger"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, tilleggsopplysninger, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private FontAwareCos nySide(FontAwarePdfDocument doc, FontAwareCos cos, PDPage scratch,
                                FontAwareCos scratchcos, boolean erEndring, InnsendingPersonInfo person) throws IOException {
        cos.close();
        header(doc, scratchcos, erEndring, STARTY, person);
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

    public float renderUtsettelsesPeriode(UtsettelsesPeriode utsettelse, BrukerRolle rolle, List<Vedlegg> vedlegg, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addBulletPoint(txt("utsettelse"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(utsettelse), cos, y);
        y = renderVedleggMedAutomatiskVedleggSjekk(vedlegg, utsettelse, rolle, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private List<String> uttaksData(UtsettelsesPeriode utsettelse) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", utsettelse.getFom());
        addIfSet(attributter, "tom", utsettelse.getTom());
        addIfSet(attributter, DAGER, String.valueOf(utsettelse.dager()));

        if (utsettelse.getÅrsak().getKey() != null) {
            attributter.add(txt("utsettelsesårsak", txt(utsettelse.getÅrsak().getKey())));
        } else {
            attributter.add(txt("utsettelsesårsak", cap(utsettelse.getÅrsak().name())));
        }

        addIfSet(attributter, utsettelse.getMorsAktivitetsType());
        if (!utsettelse.getÅrsak().equals(LOVBESTEMT_FERIE)) {
            attributter.add(txt("erarbeidstaker", jaNei(utsettelse.isErArbeidstaker())));
        }
        return attributter;
    }

    public float renderOppholdsPeriode(OppholdsPeriode opphold, List<Vedlegg> vedlegg, FontAwareCos cos, float y) throws IOException {
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
        y = renderVedleggMedAutomatiskVedleggSjekk(vedlegg, uttak, rolle, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    public float renderGradertPeriode(GradertUttaksPeriode gradert,
                                      BrukerRolle rolle,
                                      List<Vedlegg> vedlegg,
                                      int antallBarn,
                                      FontAwareCos cos,
                                      float y) throws IOException {
        y -= renderer.addBulletPoint(txt("gradertuttak"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, uttaksData(gradert, antallBarn, rolle), cos, y);
        y = renderVedleggMedAutomatiskVedleggSjekk(vedlegg, gradert, rolle, cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private float renderVedleggMedAutomatiskVedleggSjekk(List<Vedlegg> vedlegg,
                                                         LukketPeriodeMedVedlegg periode,
                                                         BrukerRolle brukerRolle,
                                                         FontAwareCos cos,
                                                         float y) throws IOException {
        if (skalRenderAutomatiskVedlegg(periode, brukerRolle)) {
            return renderAutomatiskVedlegg(cos, y);
        }
        return renderVedlegg(vedlegg, periode.getVedlegg(), DOKUMENTASJON, cos, y);
    }

    private boolean skalRenderAutomatiskVedlegg(LukketPeriodeMedVedlegg periodeMedVedlegg, BrukerRolle rolle) {
        if (!FAR.equals(rolle) && !MEDMOR.equals(rolle)) {
            return false;
        }

        if (!periodeMedVedlegg.getVedlegg().isEmpty()) {
            return false;
        }

        if (periodeMedVedlegg instanceof UttaksPeriode uttaksPeriode) { // Ugradert og gradert uttaksperiode likestilles
            return MorsAktivitet.ARBEID.equals(uttaksPeriode.getMorsAktivitetsType())
                && (FELLESPERIODE.equals(uttaksPeriode.getUttaksperiodeType()) || FORELDREPENGER.equals(uttaksPeriode.getUttaksperiodeType()));
        }

        if (periodeMedVedlegg instanceof UtsettelsesPeriode utsettelsesPeriode) {
            return MorsAktivitet.ARBEID.equals(utsettelsesPeriode.getMorsAktivitetsType());
        }

        return false;
    }

    private float renderAutomatiskVedlegg(FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLineOfRegularText(INDENT, txt("vedlegg1"), cos, y);
        y -= renderer.addBulletPoint(INDENT,
            txt("vedlegg2", "Ingen", "Opplysninger om mors arbeid innhentes fra Arbeidsgiver- og arbeidstakerregisteret"), cos, y);
        return y;
    }

    private List<String> uttaksData(GradertUttaksPeriode gradert, int antallBarn, BrukerRolle rolle) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fom", gradert.getFom());
        addIfSet(attributter, "tom", gradert.getTom());
        addIfSet(attributter, DAGER, String.valueOf(gradert.dager()));
        attributter.add(txt(UTTAKSPERIODETYPE, kontoTypeForRolle(gradert.getUttaksperiodeType(), rolle)));
        addIfSet(attributter, "fp.justeresvedfødsel", gradert.getJusteresVedFødsel());
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
        addIfSet(attributter, gradert.isØnskerSamtidigUttak(), "samtidiguttakprosent", String.valueOf(prosentFra(gradert.getSamtidigUttakProsent())));
        return attributter;
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
        addIfSet(attributter, "fp.justeresvedfødsel", uttak.getJusteresVedFødsel());
        addIfSet(attributter, uttak.getMorsAktivitetsType());
        if (antallBarn > 1) {
            attributter.add(txt("ønskerflerbarnsdager", jaNei(uttak.isØnskerFlerbarnsdager())));
        }
        attributter.add(txt("ønskersamtidiguttak", jaNei(uttak.isØnskerSamtidigUttak())));
        addIfSet(attributter, uttak.isØnskerSamtidigUttak(), "samtidiguttakprosent", String.valueOf(prosentFra(uttak.getSamtidigUttakProsent())));
        return attributter;
    }

    private String kontoTypeForRolle(StønadskontoType type, BrukerRolle rolle) {
        if (MEDMOR.equals(rolle) && FEDREKVOTE.equals(type)) {
            return txt("uttakfedrekvotemedmor");
        }
        return cap(type.name());
    }

    private static List<LukketPeriodeMedVedlegg> sorted(List<LukketPeriodeMedVedlegg> perioder) {
        perioder.sort(Comparator.comparing(LukketPeriodeMedVedlegg::getFom));
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

    public float relasjonTilBarn(RelasjonTilBarn relasjon, List<Vedlegg> vedlegg, FontAwareCos cos, float y) throws IOException {
        y -= PdfElementRenderer.BLANK_LINE;
        y = omBarn(relasjon, cos, y);
        y = renderVedlegg(vedlegg, relasjon.getVedlegg(), "vedleggrelasjondok", cos, y);
        y -= PdfElementRenderer.BLANK_LINE;
        return y;
    }

    private List<String> søker(InnsendingPersonInfo person) {
        return asList(textFormatter.navn(person.navn()), textFormatter.fromMessageSource("fødselsnummerinline", person.fnr().value()));
    }

    private List<String> utenlandskForelder(UtenlandskForelder utenlandsForelder) {
        List<String> attributter = new ArrayList<>();
        attributter.add(Optional.ofNullable(utenlandsForelder.navn()).map(n -> txt("navninline", n)).orElse("Ukjent"));
        attributter.add(txt("nasjonalitetinline", textFormatter.countryName(utenlandsForelder.land(), utenlandsForelder.land().getName())));
        addIfSet(attributter, "utenlandskid", utenlandsForelder.id());
        return attributter;
    }

    private List<String> norskForelder(NorskForelder norskForelder) {
        return asList(Optional.ofNullable(norskForelder.navn()).map(n -> txt("navninline", n)).orElse("Ukjent"),
            txt("fnr", norskForelder.fnr().value()));
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

    private void addIfSet(List<String> attributter, String key, List<LocalDate> datoer) {
        if (!CollectionUtils.isEmpty(datoer)) {
            attributter.add(txt(key, textFormatter.datoer(datoer)));
        }
    }

    private void addIfSet(List<String> attributter, ÅpenPeriode periode) {
        if (periode != null) {
            addIfSet(attributter, "fom", periode.fom());
            addIfSet(attributter, "tom", periode.tom());
        }
    }

    private String jaNei(Boolean value) {
        return jaNei(toBoolean(value));
    }

    private List<String> barn(RelasjonTilBarn relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel fødselRelasjon) {
            return fødsel(fødselRelasjon);
        }
        if (relasjonTilBarn instanceof Adopsjon adopsjonRelasjon) {
            return adopsjon(adopsjonRelasjon);
        }
        if (relasjonTilBarn instanceof FremtidigFødsel fremtidigFødselRelasjon) {
            return termin(fremtidigFødselRelasjon);
        }
        if (relasjonTilBarn instanceof Omsorgsovertakelse omsorgsovertakelseRelasjon) {
            return omsorgsovertakelse(omsorgsovertakelseRelasjon);
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
        addIfSet(attributter, "omsorgsovertakelsesdato", overtakelse.getOmsorgsovertakelsesdato());
        addIfSet(attributter, FØDSELSDATO, overtakelse.getFødselsdato());
        return attributter;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [renderer=" + renderer + ", textFormatter=" + textFormatter + "]";
    }

    public void addOutlineItem(FontAwarePdfDocument doc, PDPage page, PdfOutlineItem title) {
        renderer.addOutlineItem(doc, page, title);
    }
}
