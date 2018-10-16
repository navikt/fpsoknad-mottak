package no.nav.foreldrepenger.mottak.innsending.pdf;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000060;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
        y -= renderer.addCenteredHeading(endring ? txt("endringsøknad_fp") : txt("søknad_fp"), cos, y);
        y -= renderer.addCenteredHeadings(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float annenForelder(AnnenForelder annenForelder, boolean erAnnenForlderInformert,
            boolean harAnnenForelderRett,
            PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(txt("omfar"), cos, y);
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
            y -= renderer.addLineOfRegularText(INDENT, txt("harrett") +
                    textFormatter.yesNo(harAnnenForelderRett), cos, y);
            y -= renderer.addLineOfRegularText(INDENT,
                    txt("informert") + textFormatter.yesNo(erAnnenForlderInformert), cos,
                    y);
        }
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float dekningsgrad(Dekningsgrad dekningsgrad, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(txt("dekningsgrad"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, dekningsgrad.kode() + "%", cos, y);
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float rettigheter(Rettigheter rettigheter, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(txt("rettigheter"), cos, y);
        y -= renderer.addLineOfRegularText(txt("aleneomsorg") +
                textFormatter.yesNo(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        y -= renderer.addLineOfRegularText(txt("omsorgiperiodene") +
                textFormatter.yesNo(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        return startY - y;

    }

    public float opptjening(Opptjening opptjening, List<Arbeidsforhold> arbeidsforhold, List<Vedlegg> vedlegg,
            PDPageContentStream cos,
            float y) throws IOException {
        float startY = y;

        y = arbeidsforholdOpptjening(arbeidsforhold, cos, y);
        y = utenlandskeArbeidsforholdOpptjening(opptjening.getUtenlandskArbeidsforhold(), vedlegg, cos, y);
        y = annenOpptjening(opptjening.getAnnenOpptjening(), vedlegg, cos, y);
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

    private float annenOpptjening(List<AnnenOpptjening> annenOpptjening, List<Vedlegg> vedlegg, PDPageContentStream cos,
            float y)
            throws IOException {
        if (CollectionUtils.isEmpty(annenOpptjening)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("annenopptjening"), cos, y);
        for (AnnenOpptjening annen : annenOpptjening) {
            y -= renderer.addBulletPoint(
                    txt("type", textFormatter.capitalize(annen.getType().name())), cos, y);
            if (annen.getPeriode() != null) {
                y -= renderer.addLineOfRegularText(INDENT, txt("fom",
                        textFormatter.date(annen.getPeriode().getFom())), cos, y);
                if (annen.getPeriode().getTom() != null) {
                    y -= renderer.addLineOfRegularText(INDENT, txt("tom",
                            textFormatter.date(annen.getPeriode().getTom())), cos, y);
                }
            }
            y -= renderer.addLineOfRegularText(INDENT, txt("vedlegg1"), cos, y);
            y = renderVedlegg(vedlegg, annen.getVedlegg(), "vedleggannenopptjening", cos, y);
        }
        y -= renderer.addBlankLine();
        return y;

    }

    public float egneNæringerOpptjening(List<EgenNæring> egneNæringer, PDPageContentStream cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(egneNæringer)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("egennæring"), cos, y);
        for (EgenNæring egenNæring : egneNæringer) {
        }
        for (List<String> næring : egenNæring(egneNæringer)) {
            y -= renderer.addLMultilineBulletpoint(næring, cos, y);
        }
        return y;

    }

    public float utenlandskeArbeidsforholdOpptjening(List<UtenlandskArbeidsforhold> utenlandskArbeidsforhold,
            List<Vedlegg> vedlegg, PDPageContentStream cos,
            float y) throws IOException {
        if (CollectionUtils.isEmpty(utenlandskArbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("utenlandskarbeid"), cos, y);
        for (UtenlandskArbeidsforhold forhold : utenlandskArbeidsforhold) {
            y -= renderer.addBulletPoint(
                    txt("arbeidsgiver",
                            Optional.ofNullable(forhold.getArbeidsgiverNavn()).orElse("Ikke oppgitt")),
                    cos,
                    y);
            y -= renderer.addLinesOfRegularText(INDENT, utenlandskeArbeidsforhold(forhold), cos, y);
            y -= renderer.addLineOfRegularText(INDENT, txt("vedlegg1"), cos, y);
            y = renderVedlegg(vedlegg, forhold.getVedlegg(), "vedleggutenlandskarbeid", cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    private float renderVedlegg(List<Vedlegg> vedlegg, List<String> vedleggRefs, String keyIfAnnet,
            PDPageContentStream cos,
            float y) throws IOException {
        for (String id : vedleggRefs) {
            Vedlegg details = vedlegg.stream().filter(s -> s.getId().equals(id)).findFirst().get();
            String beskrivelse = vedleggsBeskrivelse(keyIfAnnet, details);
            y -= renderer.addBulletPoint(INDENT, txt("vedlegg2", beskrivelse,
                    textFormatter.capitalize(details.getInnsendingsType().name())), cos, y);
        }
        return y;
    }

    public float arbeidsforholdOpptjening(List<Arbeidsforhold> arbeidsforhold, PDPageContentStream cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(arbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("arbeidsforhold"), cos, y);

        for (Arbeidsforhold forhold : arbeidsforhold) {
            y -= renderer.addBulletPoint(txt("arbeidsgiver", forhold.getArbeidsgiverNavn()),
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
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
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
            sb.append(", " + txt("nærrelasjon"));
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
        y -= renderer.addLeftHeading(txt("medlemsskap"), cos, y);
        TidligereOppholdsInformasjon tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        y -= renderer.addLineOfRegularText(INDENT, txt("siste12") + " " +
                (tidligereOpphold.isBoddINorge() ? "Norge" : "utlandet"), cos, y);
        if (tidligereOpphold.getUtenlandsOpphold().size() != 0) {
            y -= renderer.addLeftHeading(txt("tidligereopphold"), cos, y);
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(tidligereOpphold.getUtenlandsOpphold()), cos, y);
        }
        FramtidigOppholdsInformasjon framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();
        y -= renderer.addLineOfRegularText(txt("neste12") + " " +
                (framtidigeOpphold.isNorgeNeste12() ? "Norge" : "utlandet"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("føderi",
                (framtidigeOpphold.isFødselNorge() ? "Norge" : "utlandet")), cos, y);
        if (framtidigeOpphold.getUtenlandsOpphold().size() != 0) {
            y -= renderer.addLeftHeading(txt("framtidigeopphold"), cos, y);
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(framtidigeOpphold.getUtenlandsOpphold()), cos,
                    y);
        }
        return startY - y;
    }

    public float omBarn(RelasjonTilBarnMedVedlegg relasjon, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(txt("barn"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, barn(relasjon), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("antallbarn", relasjon.getAntallBarn()), cos, y);
        return startY - y;
    }

    public float fordeling(Fordeling fordeling, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(txt("perioder"), cos, y);
        Collections.sort(fordeling.getPerioder(), new Comparator<LukketPeriodeMedVedlegg>() {

            @Override
            public int compare(LukketPeriodeMedVedlegg o1, LukketPeriodeMedVedlegg o2) {
                return o1.getFom().compareTo(o2.getFom());
            }

        });
        for (LukketPeriodeMedVedlegg periode : fordeling.getPerioder()) {
            y -= renderer.addBulletPoint(periode(periode), cos, y);
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
                    txt("fom", textFormatter.date(overføring.getFom())),
                    txt("tom", textFormatter.date(overføring.getTom())),
                    txt("uttaksperiodetype",
                            textFormatter.capitalize(overføring.getUttaksperiodeType().name())),
                    txt("overføringsårsak",
                            textFormatter.capitalize(overføring.getÅrsak().name())));

        }
        if (periode instanceof GradertUttaksPeriode) {
            GradertUttaksPeriode gradert = GradertUttaksPeriode.class.cast(periode);
            ArrayList<String> attributter = newArrayList(
                    txt("fom", textFormatter.date(gradert.getFom())),
                    txt("tom", textFormatter.date(gradert.getTom())),
                    txt("uttaksperiodetype", textFormatter.capitalize(gradert.getUttaksperiodeType().name())),
                    txt("virksomhetsnummer", gradert.getVirksomhetsnummer()),
                    txt("skalgraderes",
                            textFormatter.yesNo(gradert.isArbeidsForholdSomskalGraderes())),
                    txt("erarbeidstaker",
                            textFormatter.yesNo(gradert.isErArbeidstaker())),
                    txt("ønskerflerbarnsdager",
                            textFormatter.yesNo(gradert.isØnskerFlerbarnsdager())),
                    txt("ønskersamtidiguttak",
                            textFormatter.yesNo(gradert.isØnskerSamtidigUttak())));
            if (gradert.getMorsAktivitetsType() != null) {
                attributter.add(txt("morsaktivitet",
                        textFormatter.capitalize(gradert.getMorsAktivitetsType().name())));
            }
            if (gradert.isØnskerSamtidigUttak()) {
                attributter.add(txt("samtidiguttakprosent",
                        String.valueOf(gradert.getSamtidigUttakProsent())));
            }
            return attributter;
        }
        if (periode instanceof UttaksPeriode) {
            UttaksPeriode uttak = UttaksPeriode.class.cast(periode);
            ArrayList<String> attributter = newArrayList(
                    txt("fom", textFormatter.date(uttak.getFom())),
                    txt("tom", textFormatter.date(uttak.getTom())),
                    txt("uttaksperiodetype", textFormatter.capitalize(uttak.getUttaksperiodeType().name())),
                    txt("ønskerflerbarnsdager",
                            textFormatter.yesNo(uttak.isØnskerFlerbarnsdager())),
                    txt("ønskersamtidiguttak",
                            textFormatter.yesNo(uttak.isØnskerSamtidigUttak())));
            if (uttak.getMorsAktivitetsType() != null) {
                attributter.add(txt("morsaktivitet",
                        textFormatter.capitalize(uttak.getMorsAktivitetsType().name())));
            }
            if (uttak.isØnskerSamtidigUttak()) {
                attributter.add(txt("samtidiguttakprosent",
                        String.valueOf(uttak.getSamtidigUttakProsent())));
            }
            return attributter;
        }
        if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode opphold = OppholdsPeriode.class.cast(periode);
            return newArrayList(
                    txt("fom", textFormatter.date(opphold.getFom())),
                    txt("tom", textFormatter.date(opphold.getTom())),
                    txt("oppholdsårsak", textFormatter.capitalize(opphold.getÅrsak().name())));
        }
        if (periode instanceof UtsettelsesPeriode) {
            UtsettelsesPeriode utsettelse = UtsettelsesPeriode.class.cast(periode);
            return newArrayList(
                    txt("fom", textFormatter.date(utsettelse.getFom())),
                    txt("tom", textFormatter.date(utsettelse.getTom())),
                    txt("uttaksperiodetype", textFormatter.capitalize(utsettelse.getUttaksperiodeType().name())),
                    txt("utsettelsesårsak",
                            textFormatter.capitalize(utsettelse.getÅrsak().name())),
                    txt("virksomhetsnummer", utsettelse.getVirksomhetsnummer()),
                    txt("erarbeidstaker",
                            textFormatter.yesNo(utsettelse.isErArbeidstaker())));
        }

        throw new IllegalArgumentException(periode.getClass().getSimpleName() + " ikke støttet");
    }

    public float relasjonTilBarn(RelasjonTilBarnMedVedlegg relasjon, List<Vedlegg> vedlegg, PDPageContentStream cos,
            float y)
            throws IOException {
        float startY = y;
        y -= renderer.addBlankLine();
        y -= omBarn(relasjon, cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("vedlegg1"), cos, y);
        y = renderVedlegg(vedlegg, relasjon.getVedlegg(), "vedleggrelasjondok", cos, y);
        y -= renderer.addBlankLine();
        return startY - y;
    }

    public float vedlegg(List<Vedlegg> vedlegg, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(txt("vedlegg"), cos, y);
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
                .map(n -> txt("navn", n))
                .orElse("Ukjent"));
        lines.add(txt("nasjonalitet",
                textFormatter.countryName(utenlandsForelder.getLand().getAlpha2(),
                        utenlandsForelder.getLand().getName())));
        if (utenlandsForelder.getId() != null) {
            lines.add(txt("utenlandskid", utenlandsForelder.getId()));
        }
        return lines;
    }

    private List<String> norskForelder(NorskForelder norskForelder) {
        return Arrays.asList(
                Optional.ofNullable(norskForelder.getNavn())
                        .map(n -> txt("navn", n))
                        .orElse("Ukjent"),
                txt("nasjonalitet", "Norsk"),
                txt("fnr", norskForelder.getFnr().getFnr()));
    }

    private List<String> arbeidsforhold(Arbeidsforhold arbeidsforhold) {
        List<String> attributter = Lists
                .newArrayList(txt("fom", textFormatter.date(arbeidsforhold.getFrom())));
        if (arbeidsforhold.getTo().isPresent()) {
            attributter.add(txt("tom", textFormatter.date(arbeidsforhold.getTo().get())));
        }
        attributter
                .add(txt("stillingsprosent", arbeidsforhold.getStillingsprosent()));
        return attributter;
    }

    private List<String> utenlandskeArbeidsforhold(UtenlandskArbeidsforhold ua) {
        List<String> attributter = Lists
                .newArrayList(txt("fom", textFormatter.date(ua.getPeriode().getFom())));
        if (ua.getPeriode().getTom() != null) {
            attributter.add(txt("tom", textFormatter.date(ua.getPeriode().getTom())));
        }
        if (ua.getLand() != null) {
            attributter
                    .add(txt("land", textFormatter.countryName(ua.getLand().getAlpha2())));
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
            sb.append(txt("varigendring"));
        }

        if (næring.isErNyOpprettet()) {
            sb.append(", " + txt("nyopprettet"));
        }

        if (næring.isErNyIArbeidslivet()) {
            sb.append(", " + txt("nyiarbeidslivet"));
        }

        if (næring.isNærRelasjon()) {
            sb.append(", " + txt("nærrelasjon"));
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
        if (relasjonTilBarn instanceof Adopsjon) {
            Adopsjon adopsjon = Adopsjon.class.cast(relasjonTilBarn);
            return Collections.singletonList("Adopsjon: " + adopsjon.toString());
        }
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel fødsel = FremtidigFødsel.class.cast(relasjonTilBarn);
            return newArrayList("Fødsel med termin: " + textFormatter.date(fødsel.getTerminDato()),
                    "Bekreftelse utstedt " + textFormatter.date(fødsel.getUtstedtDato()));
        }

        Omsorgsovertakelse omsorgsovertakelse = Omsorgsovertakelse.class.cast(relasjonTilBarn);
        return Collections.singletonList("Omsorgsovertakelse: " + omsorgsovertakelse.getOmsorgsovertakelsesdato() +
                ", " + omsorgsovertakelse.getÅrsak());

    }

    private String periode(LukketPeriodeMedVedlegg periode) {
        if (periode instanceof OverføringsPeriode) {
            return txt("overføring");
        }
        if (periode instanceof GradertUttaksPeriode) {
            return txt("gradertuttak");
        }
        if (periode instanceof UttaksPeriode) {
            return txt("uttak");
        }
        if (periode instanceof OppholdsPeriode) {
            return txt("opphold");
        }
        if (periode instanceof UtsettelsesPeriode) {
            return txt("utsettelse");
        }

        throw new IllegalArgumentException(periode.getClass().getSimpleName() + " ikke støttet");

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
