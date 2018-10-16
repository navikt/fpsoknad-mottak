package no.nav.foreldrepenger.mottak.innsending.pdf;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
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
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskOrganisasjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening;
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

public class ForeldrepengeInfoRenderer {
    private static final int INDENT = 20;
    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public ForeldrepengeInfoRenderer(MessageSource landkoder, MessageSource kvitteringstekster) {
        renderer = new PDFElementRenderer();
        textFormatter = new SøknadTextFormatter(landkoder, kvitteringstekster, CountryCode.NO);
    }

    public float header(Person søker, PDDocument doc, PDPageContentStream cos, boolean endring, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(endring ? textFormatter.fromMessageSource("endringsøknad_fp")
                : textFormatter.fromMessageSource("søknad_fp"), cos, y);
        y -= renderer.addCenteredHeadings(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float annenForelder(AnnenForelder annenForelder, boolean erAnnenForlderInformert,
            boolean harAnnenForelderRett,
            PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("omfar"), cos, y);
        if (annenForelder instanceof NorskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, norskForelder(NorskForelder.class.cast(annenForelder)), cos, y);
        }
        else if (annenForelder instanceof UtenlandskForelder) {
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskForelder(annenForelder), cos, y);
        }
        else {
            y -= renderer.addLineOfRegularText(INDENT, "Ukjent", cos, y);
        }

        if (!(annenForelder instanceof UkjentForelder)) {
            y -= renderer.addLineOfRegularText(INDENT, textFormatter.fromMessageSource("harrett") +
                    textFormatter.yesNo(harAnnenForelderRett), cos, y);
            y -= renderer.addLineOfRegularText(INDENT,
                    textFormatter.fromMessageSource("informert") + textFormatter.yesNo(erAnnenForlderInformert), cos,
                    y);
        }
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float dekningsgrad(Dekningsgrad dekningsgrad, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("dekningsgrad"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, dekningsgrad.kode() + "%", cos, y);
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float rettigheter(Rettigheter rettigheter, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("rettigheter"), cos, y);
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("aleneomsorg") +
                textFormatter.yesNo(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("omsorgiperiodene") +
                textFormatter.yesNo(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        return startY - y;

    }

    public float opptjening(Opptjening opptjening, List<Arbeidsforhold> arbeidsforhold, PDPageContentStream cos,
            float y) throws IOException {
        float startY = y;

        y = arbeidsforholdOpptjening(arbeidsforhold, cos, y);
        y = utenlandskeArbeidsforholdOpptjening(opptjening.getUtenlandskArbeidsforhold(), cos, y);
        y = annenOpptjening(opptjening.getAnnenOpptjening(), cos, y);
        y = egneNæringerOpptjening(opptjening.getEgenNæring(), cos, y);
        y = frilansOpptjening(opptjening.getFrilans(), cos, y);
        return startY - y;
    }

    public float frilansOpptjening(Frilans frilans, PDPageContentStream cos, float y) throws IOException {
        if (frilans == null) {
            return y;
        }
        y -= frilans(frilans, cos, y);
        return y;
    }

    private float annenOpptjening(List<AnnenOpptjening> annenOpptjening, PDPageContentStream cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(annenOpptjening)) {
            return y;
        }
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("annenopptjening"), cos, y);
        for (AnnenOpptjening annen : annenOpptjening) {
            y -= renderer.addBulletPoint(
                    textFormatter.fromMessageSource("type", textFormatter.capitalize(annen.getType().name())), cos, y);
            if (annen.getPeriode() != null) {
                y -= renderer.addLineOfRegularText(INDENT, textFormatter.fromMessageSource("fom",
                        textFormatter.date(annen.getPeriode().getFom())), cos, y);
                if (annen.getPeriode().getTom() != null) {
                    y -= renderer.addLineOfRegularText(INDENT, textFormatter.fromMessageSource("tom",
                            textFormatter.date(annen.getPeriode().getTom())), cos, y);
                }
            }
        }
        y -= renderer.addBlankLine();
        return y;

    }

    public float egneNæringerOpptjening(List<EgenNæring> egneNæringer, PDPageContentStream cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(egneNæringer)) {
            return y;
        }
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("egennæring"), cos, y);
        for (EgenNæring egenNæring : egneNæringer) {
        }
        for (List<String> næring : egenNæring(egneNæringer)) {
            y -= renderer.addLMultilineBulletpoint(næring, cos, y);
        }
        return y;

    }

    public float utenlandskeArbeidsforholdOpptjening(List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold,
            PDPageContentStream cos,
            float y) throws IOException {
        if (CollectionUtils.isEmpty(utenlandskArbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("utenlandskarbeid"), cos, y);
        for (UtenlandskArbeidsforhold forhold : utenlandskArbeidsforhold) {
            y -= renderer.addBulletPoint(
                    textFormatter.fromMessageSource("arbeidsgiver",
                            Optional.ofNullable(forhold.getArbeidsgiverNavn()).orElse("Ikke oppgitt")),
                    cos,
                    y);
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskeArbeidsforhold(forhold), cos, y);
        }
        y -= renderer.addBlankLine();
        // y -=
        // renderer.addBulletList(utenlandskeArbeidsforhold(utenlandskArbeidsforhold),
        // cos, y);
        return y;
    }

    public float arbeidsforholdOpptjening(List<Arbeidsforhold> arbeidsforhold, PDPageContentStream cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(arbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("arbeidsforhold"), cos, y);

        for (Arbeidsforhold forhold : arbeidsforhold) {
            y -= renderer.addBulletPoint(textFormatter.fromMessageSource("arbeidsgiver", forhold.getArbeidsgiverNavn()),
                    cos,
                    y);
            y -= renderer.addLinesOfRegularText(INDENT, arbeidsforhold(forhold), cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    public float frilans(Frilans frilans, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addBlankLine();
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("frilans"), cos, y);
        StringBuilder sb = new StringBuilder(textFormatter.periode(frilans.getPeriode()));
        if (frilans.isHarInntektFraFosterhjem()) {
            sb.append(", har fra fosterhjem");
        }
        if (frilans.isNyOppstartet()) {
            sb.append(", nyoppstartet");
        }
        if (frilans.getVedlegg().size() != 0) {
            sb.append(", vedlegg: " + frilans.getVedlegg().stream().collect(joining(", ")));
        }
        if (frilans.getFrilansOppdrag().size() != 0) {
            sb.append(", " + textFormatter.fromMessageSource("nærrelasjon"));
        }
        y -= renderer.addLineOfRegularText(sb.toString(), cos, y);
        List<String> oppdrag = frilans.getFrilansOppdrag().stream()
                .map(o -> o.getOppdragsgiver() + " " + textFormatter.periode(o.getPeriode()))
                .collect(toList());
        y -= renderer.addBulletList(oppdrag, cos, y);
        return startY - y;
    }

    public float medlemsskap(Medlemsskap medlemsskap, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("medlemsskap"), cos, y);
        TidligereOppholdsInformasjon tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        y -= renderer.addLineOfRegularText(INDENT, textFormatter.fromMessageSource("siste12") + " " +
                (tidligereOpphold.isBoddINorge() ? "Norge" : "utlandet"), cos, y);
        if (tidligereOpphold.getUtenlandsOpphold().size() != 0) {
            y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tidligereopphold"), cos, y);
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(tidligereOpphold.getUtenlandsOpphold()), cos, y);
        }
        FramtidigOppholdsInformasjon framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("neste12") + " " +
                (framtidigeOpphold.isNorgeNeste12() ? "Norge" : "utlandet"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, textFormatter.fromMessageSource("føderi",
                (framtidigeOpphold.isFødselNorge() ? "Norge" : "utlandet")), cos, y);
        if (framtidigeOpphold.getUtenlandsOpphold().size() != 0) {
            y -= renderer.addLeftHeading(textFormatter.fromMessageSource("framtidigeopphold"), cos, y);
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(framtidigeOpphold.getUtenlandsOpphold()), cos,
                    y);
        }
        return startY - y;
    }

    public float omBarn(RelasjonTilBarnMedVedlegg relasjon, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("barn"), cos, y);
        y -= renderer.addLinesOfRegularText(barn(relasjon), cos, y);
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("antallbarn", relasjon.getAntallBarn()), cos,
                y);
        return startY - y;
    }

    public float fordeling(Fordeling fordeling, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("perioder"), cos, y);
        for (LukketPeriodeMedVedlegg periode : fordeling.getPerioder()) {
            y -= renderer.addBulletPoint(formatterPeriode(periode), cos, y);
            y -= renderer.addLinesOfRegularText(INDENT, periodeDataFra(periode), cos, y);
            y -= renderer.addBlankLine();
        }
        y -= renderer.addBlankLine();
        return startY - y;
    }

    private List<String> periodeDataFra(LukketPeriodeMedVedlegg periode) {
        if (periode instanceof OverføringsPeriode) {
            OverføringsPeriode overføring = OverføringsPeriode.class.cast(periode);
            return newArrayList(
                    textFormatter.fromMessageSource("uttaksperiodetype",
                            textFormatter.capitalize(overføring.getUttaksperiodeType().name())),
                    textFormatter.fromMessageSource("overføringsårsak",
                            textFormatter.capitalize(overføring.getÅrsak().name())));

        }
        if (periode instanceof GradertUttaksPeriode) {
            GradertUttaksPeriode gradert = GradertUttaksPeriode.class.cast(periode);
            ArrayList<String> attributter = newArrayList(textFormatter.fromMessageSource("uttaksperiodetype",
                    textFormatter.capitalize(gradert.getUttaksperiodeType().name())),
                    textFormatter.fromMessageSource("samtidiguttakprosent",
                            String.valueOf(gradert.getSamtidigUttakProsent())),
                    textFormatter.fromMessageSource("virksomhetsnummer", gradert.getVirksomhetsnummer()),
                    textFormatter.fromMessageSource("skalgraderes",
                            textFormatter.yesNo(gradert.isArbeidsForholdSomskalGraderes())),
                    textFormatter.fromMessageSource("erarbeidstaker",
                            textFormatter.yesNo(gradert.isErArbeidstaker())),
                    textFormatter.fromMessageSource("ønskerflerbarnsdager",
                            textFormatter.yesNo(gradert.isØnskerFlerbarnsdager())),
                    textFormatter.fromMessageSource("ønskersamtidiguttak",
                            textFormatter.yesNo(gradert.isØnskerSamtidigUttak())));
            if (gradert.getMorsAktivitetsType() != null) {
                attributter.add(textFormatter.fromMessageSource("morsaktivitet",
                        textFormatter.capitalize(gradert.getMorsAktivitetsType().name())));
            }
            return attributter;
        }
        if (periode instanceof UttaksPeriode) {
            UttaksPeriode uttak = UttaksPeriode.class.cast(periode);
            ArrayList<String> attributter = newArrayList(textFormatter.fromMessageSource("uttaksperiodetype",
                    textFormatter.capitalize(uttak.getUttaksperiodeType().name())),
                    textFormatter.fromMessageSource("samtidiguttakprosent",
                            String.valueOf(uttak.getSamtidigUttakProsent())),
                    textFormatter.fromMessageSource("ønskerflerbarnsdager",
                            textFormatter.yesNo(uttak.isØnskerFlerbarnsdager())),
                    textFormatter.fromMessageSource("ønskersamtidiguttak",
                            textFormatter.yesNo(uttak.isØnskerSamtidigUttak())));
            if (uttak.getMorsAktivitetsType() != null) {
                attributter.add(textFormatter.fromMessageSource("morsaktivitet",
                        textFormatter.capitalize(uttak.getMorsAktivitetsType().name())));
            }
            return attributter;
        }
        if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode opphold = OppholdsPeriode.class.cast(periode);
            return newArrayList(
                    textFormatter.fromMessageSource("oppholdsårsak",
                            textFormatter.capitalize(opphold.getÅrsak().name())));
        }
        if (periode instanceof UtsettelsesPeriode) {
            UtsettelsesPeriode utsettelse = UtsettelsesPeriode.class.cast(periode);
            return newArrayList(textFormatter.fromMessageSource("uttaksperiodetype",
                    textFormatter.capitalize(utsettelse.getUttaksperiodeType().name())),
                    textFormatter.fromMessageSource("utsettelsesårsak",
                            textFormatter.capitalize(utsettelse.getÅrsak().name())),
                    textFormatter.fromMessageSource("virksomhetsnummer", utsettelse.getVirksomhetsnummer()),
                    textFormatter.fromMessageSource("erarbeidstaker",
                            textFormatter.yesNo(utsettelse.isErArbeidstaker())));
        }

        throw new IllegalArgumentException(periode.getClass().getSimpleName() + " ikke støttet");
    }

    public float relasjonTilBarn(RelasjonTilBarnMedVedlegg relasjon, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBlankLine();
        y -= omBarn(relasjon, cos, y);
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float vedlegg(List<Vedlegg> vedlegg, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("vedlegg"), cos, y);
        List<String> formatted = vedlegg.stream()
                .map(textFormatter::vedlegg)
                .collect(toList());
        y -= renderer.addBulletList(formatted, cos, y);
        return startY - y;
    }

    public String regnskapsførere(List<Regnskapsfører> regnskapsførere) {
        return regnskapsførere == null ? "ukjent"
                : regnskapsførere.stream()
                        .map(this::format)
                        .collect(joining(","));
    }

    private String format(Regnskapsfører regnskapsfører) {
        return regnskapsfører.getNavn() + " (" +
                Optional.ofNullable(regnskapsfører.getTelefon()).orElse("ukjent tlfnr") + ")";
    }

    private List<String> søker(Person søker) {
        String fnr = søker.fnr.getFnr();
        String navn = textFormatter.navn(søker);
        return Arrays.asList(fnr,
                navn != null ? navn : "ukjent");
    }

    private List<String> utenlandskForelder(AnnenForelder annenForelder) {
        UtenlandskForelder utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        List<String> lines = new ArrayList<>();
        lines.add(Optional.ofNullable(utenlandsForelder.getNavn())
                .map(n -> textFormatter.fromMessageSource("navn", n))
                .orElse("Ukjent"));
        lines.add(textFormatter.fromMessageSource("nasjonalitet",
                textFormatter.countryName(utenlandsForelder.getLand().getAlpha2(),
                        utenlandsForelder.getLand().getName())));
        if (utenlandsForelder.getId() != null) {
            lines.add(textFormatter.fromMessageSource("utenlandskid", utenlandsForelder.getId()));
        }
        return lines;
    }

    private List<String> norskForelder(NorskForelder norskForelder) {
        return Arrays.asList(
                Optional.ofNullable(norskForelder.getNavn())
                        .map(n -> textFormatter.fromMessageSource("navn", n))
                        .orElse("Ukjent"),
                textFormatter.fromMessageSource("nasjonalitet", "Norsk"),
                textFormatter.fromMessageSource("fnr", norskForelder.getFnr().getFnr()));
    }

    private List<String> arbeidsforhold(Arbeidsforhold arbeidsforhold) {
        List<String> attributter = Lists
                .newArrayList(textFormatter.fromMessageSource("fom", textFormatter.date(arbeidsforhold.getFrom())));
        if (arbeidsforhold.getTo().isPresent()) {
            attributter.add(textFormatter.fromMessageSource("tom", textFormatter.date(arbeidsforhold.getTo().get())));
        }
        attributter
                .add(textFormatter.fromMessageSource("stillingsprosent", arbeidsforhold.getStillingsprosent()));
        return attributter;
    }

    private List<String> utenlandskeArbeidsforhold(UtenlandskArbeidsforhold ua) {
        List<String> attributter = Lists
                .newArrayList(textFormatter.fromMessageSource("fom", textFormatter.date(ua.getPeriode().getFom())));
        if (ua.getPeriode().getTom() != null) {
            attributter.add(textFormatter.fromMessageSource("tom", textFormatter.date(ua.getPeriode().getTom())));
        }
        if (ua.getLand() != null) {
            attributter
                    .add(textFormatter.fromMessageSource("land", textFormatter.countryName(ua.getLand().getAlpha2())));
        }
        return attributter;
    }

    private List<List<String>> egenNæring(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(this::format)
                .collect(toList());
    }

    private List<String> format(EgenNæring næring) {
        CountryCode arbeidsland = Optional.ofNullable(næring.getArbeidsland()).orElse(CountryCode.NO);
        String typer = næring.getVirksomhetsTyper().stream()
                .map(v -> textFormatter.capitalize(v.toString()))
                .collect(joining(","));
        StringBuilder sb = new StringBuilder(typer + " i " + textFormatter.countryName(arbeidsland.getAlpha2()));
        sb.append(" hos ");
        if (næring instanceof NorskOrganisasjon) {
            NorskOrganisasjon org = NorskOrganisasjon.class.cast(næring);
            sb.append(org.getOrgName());
            sb.append(" (" + org.getOrgNummer() + ")");
        }
        else {
            UtenlandskOrganisasjon org = UtenlandskOrganisasjon.class.cast(næring);
            sb.append(org.getOrgName());
        }
        sb.append(" " + textFormatter.periode(næring.getPeriode()));

        sb.append("\n");
        if (næring.isErVarigEndring()) {
            sb.append(textFormatter.fromMessageSource("varigendring"));
        }

        if (næring.isErNyOpprettet()) {
            sb.append(", " + textFormatter.fromMessageSource("nyopprettet"));
        }

        if (næring.isErNyIArbeidslivet()) {
            sb.append(", " + textFormatter.fromMessageSource("nyiarbeidslivet"));
        }

        if (næring.isNærRelasjon()) {
            sb.append(", " + textFormatter.fromMessageSource("nærrelasjon"));
        }
        sb.append("\n");

        sb.append("Brutto inntekt: " + næring.getNæringsinntektBrutto());
        sb.append(", regnskap: " + regnskapsførere(næring.getRegnskapsførere()));
        sb.append("\n");

        sb.append(næring.getBeskrivelseEndring());

        return Arrays.asList(sb.toString().split("\n"));
    }

    private List<String> barn(RelasjonTilBarnMedVedlegg relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel) {
            Fødsel fødsel = Fødsel.class.cast(relasjonTilBarn);
            return Collections.singletonList("Fødselsdato: " + textFormatter.dates(fødsel.getFødselsdato()));
        }
        else if (relasjonTilBarn instanceof Adopsjon) {
            Adopsjon adopsjon = Adopsjon.class.cast(relasjonTilBarn);
            return Collections.singletonList("Adopsjon: " + adopsjon.toString());
        }
        else if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel fødsel = FremtidigFødsel.class.cast(relasjonTilBarn);
            return newArrayList("Fødsel med termin: " + textFormatter.date(fødsel.getTerminDato()),
                    "Bekreftelse utstedt " + textFormatter.date(fødsel.getUtstedtDato()));
        }
        else {
            Omsorgsovertakelse omsorgsovertakelse = Omsorgsovertakelse.class.cast(relasjonTilBarn);
            return Collections.singletonList("Omsorgsovertakelse: " + omsorgsovertakelse.getOmsorgsovertakelsesdato() +
                    ", " + omsorgsovertakelse.getÅrsak());
        }

    }

    private String formatterPeriode(LukketPeriodeMedVedlegg periode) {
        String tid = textFormatter.date(periode.getFom()) + " - " + textFormatter.date(periode.getTom());
        if (periode instanceof OverføringsPeriode) {
            return "Overføring: " + tid;
        }
        if (periode instanceof GradertUttaksPeriode) {
            return "Gradert uttak: " + tid;
        }
        if (periode instanceof UttaksPeriode) {
            return "Uttak: " + tid;
        }
        if (periode instanceof OppholdsPeriode) {
            return "Opphold: " + tid;
        }
        if (periode instanceof UtsettelsesPeriode) {
            return "Utsettelse: " + tid;
        }

        throw new IllegalArgumentException(periode.getClass().getSimpleName() + " ikke støttet");

    }

}
