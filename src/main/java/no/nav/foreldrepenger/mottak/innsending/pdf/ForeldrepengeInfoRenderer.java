package no.nav.foreldrepenger.mottak.innsending.pdf;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000060;

import java.io.IOException;
import java.time.LocalDate;
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
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(endring ? txt("endringsøknad_fp") : txt("søknad_fp"), cos, y);
        y -= renderer.addCenteredHeadings(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    public float annenForelder(AnnenForelder annenForelder, boolean erAnnenForlderInformert,
            Rettigheter rettigheter,
            PDPageContentStream cos, float y) throws IOException {
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
            y -= renderer.addLineOfRegularText(INDENT, "Ukjent", cos, y);
        }

        if (!(annenForelder instanceof UkjentForelder)) {
            y -= renderer.addLineOfRegularText(INDENT, txt("harrett", jaNei(rettigheter.isHarAnnenForelderRett())), cos,
                    y);
            y -= renderer.addLineOfRegularText(INDENT, txt("informert", jaNei(erAnnenForlderInformert)), cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    public float rettigheter(Rettigheter rettigheter, PDPageContentStream cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("rettigheter"), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("aleneomsorg") +
                jaNei(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("omsorgiperiodene") +
                jaNei(rettigheter.isHarAleneOmsorgForBarnet()), cos, y);
        return y;

    }

    public float frilansOpptjening(Frilans frilans, PDPageContentStream cos, float y) throws IOException {
        if (frilans == null) {
            return y;
        }
        y = frilans(frilans, cos, y);
        return y;
    }

    float annenOpptjening(List<AnnenOpptjening> annenOpptjening, List<Vedlegg> vedlegg, PDPageContentStream cos,
            float y)
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

    public float egneNæringerOpptjening(List<EgenNæring> egneNæringer, PDPageContentStream cos, float y)
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
            List<Vedlegg> vedlegg, PDPageContentStream cos,
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
            PDPageContentStream cos,
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

    public float arbeidsforholdOpptjening(List<Arbeidsforhold> arbeidsforhold, PDPageContentStream cos, float y)
            throws IOException {
        if (CollectionUtils.isEmpty(arbeidsforhold)) {
            return y;
        }
        y -= renderer.addLeftHeading(txt("arbeidsforhold"), cos, y);

        for (Arbeidsforhold forhold : sortArbeidsforhold(arbeidsforhold)) {
            y -= renderer.addLinesOfRegularText(INDENT, arbeidsforhold(forhold), cos, y);
            y -= renderer.addBlankLine();
        }
        return y;
    }

    private static List<Arbeidsforhold> sortArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        Collections.sort(arbeidsforhold, new Comparator<Arbeidsforhold>() {

            @Override
            public int compare(Arbeidsforhold o1, Arbeidsforhold o2) {
                return o1.getFrom().compareTo(o2.getFrom());
            }

        });
        return arbeidsforhold;
    }

    private static List<UtenlandskArbeidsforhold> sorterUtelandske(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        Collections.sort(arbeidsforhold, new Comparator<UtenlandskArbeidsforhold>() {

            @Override
            public int compare(UtenlandskArbeidsforhold o1, UtenlandskArbeidsforhold o2) {
                return o1.getPeriode().getFom().compareTo(o2.getPeriode().getFom());
            }

        });
        return arbeidsforhold;
    }

    public float frilans(Frilans frilans, PDPageContentStream cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
        List<String> attributter = new ArrayList<>();
        if (frilans.getPeriode().getTom() == null) {
            addIfSet(attributter, "frilanspågår", textFormatter.date(frilans.getPeriode().getFom()));
        }
        else {
            attributter.add(txt("frilansavsluttet", frilans.getPeriode().getFom(),
                    textFormatter.date(frilans.getPeriode().getTom())));
        }
        addIfTrue(attributter, "fosterhjem", frilans.isHarInntektFraFosterhjem());
        addIfTrue(attributter, "nyoppstartet", frilans.isNyOppstartet());

        y -= renderer.addLinesOfRegularText(INDENT, attributter, cos, y);
        if (!frilans.getFrilansOppdrag().isEmpty()) {
            y -= renderer.addLineOfRegularText(INDENT, txt("oppdrag"), cos, y);
        }

        List<String> oppdrag = frilans.getFrilansOppdrag().stream()
                .map(o -> o.getOppdragsgiver() + " " + textFormatter.periode(o.getPeriode()))
                .collect(toList());
        y -= renderer.addBulletList(INDENT, oppdrag, cos, y);
        y -= renderer.addBlankLine();
        return y;
    }

    private void addIfTrue(List<String> attributter, String key, boolean value) {
        if (value) {
            attributter.add(txt(key, jaNei(value)));
        }
    }

    public float medlemsskap(Medlemsskap medlemsskap, RelasjonTilBarnMedVedlegg relasjonTilBarn,
            PDPageContentStream cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("medlemsskap"), cos, y);
        TidligereOppholdsInformasjon tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        FramtidigOppholdsInformasjon framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();
        String land = framtidigeOpphold.isFødselNorge() ? "Norge" : "utlandet";
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            if (FremtidigFødsel.class.cast(relasjonTilBarn).getTerminDato().isBefore(LocalDate.now())) {
                y -= renderer.addLineOfRegularText(INDENT, txt("fødtei", land), cos, y);
            }
            else {
                y -= renderer.addLineOfRegularText(INDENT, txt("føderi", land), cos, y);
            }
        }
        if (relasjonTilBarn instanceof Fødsel) {
            if (Fødsel.class.cast(relasjonTilBarn).getFødselsdato().get(0).isBefore(LocalDate.now())) {
                y -= renderer.addLineOfRegularText(INDENT, txt("terminfødtei", land), cos, y);
            }
            else {
                y -= renderer.addLineOfRegularText(INDENT, txt("terminføderi", land), cos, y);
            }
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
        return y;
    }

    public float omBarn(RelasjonTilBarnMedVedlegg relasjon, PDPageContentStream cos, float y)
            throws IOException {
        y -= renderer.addLeftHeading(txt("barn"), cos, y);
        y -= renderer.addLinesOfRegularText(INDENT, barn(relasjon), cos, y);
        y -= renderer.addLineOfRegularText(INDENT, txt("antallbarn", relasjon.getAntallBarn()), cos, y);
        return y;
    }

    public float fordeling(Fordeling fordeling, Dekningsgrad dekningsgrad, List<Vedlegg> vedlegg, int antallBarn,
            PDPageContentStream cos, float y)
            throws IOException {
        y -= renderer.addLeftHeading(txt("perioder"), cos, y);
        if (dekningsgrad != null) {
            y -= renderer.addLineOfRegularText(txt("dekningsgrad", dekningsgrad.kode()), cos, y);
        }
        for (LukketPeriodeMedVedlegg periode : sorted(fordeling.getPerioder())) {
            y -= renderer.addBulletPoint(periode(periode), cos, y);
            y -= renderer.addLinesOfRegularText(INDENT, periodeDataFra(periode, antallBarn), cos, y);
            y = renderVedlegg(vedlegg, periode.getVedlegg(), "dokumentasjon", cos, y);
            y -= renderer.addBlankLine();
        }
        y -= renderer.addBlankLine();
        return y;
    }

    private static List<LukketPeriodeMedVedlegg> sorted(List<LukketPeriodeMedVedlegg> perioder) {
        Collections.sort(perioder, new Comparator<LukketPeriodeMedVedlegg>() {

            @Override
            public int compare(LukketPeriodeMedVedlegg o1, LukketPeriodeMedVedlegg o2) {
                return o1.getFom().compareTo(o2.getFom());
            }

        });
        return perioder;
    }

    private List<String> periodeDataFra(LukketPeriodeMedVedlegg periode, int antallBarn) {
        if (periode instanceof OverføringsPeriode) {
            OverføringsPeriode overføring = OverføringsPeriode.class.cast(periode);
            ArrayList<String> attributter = new ArrayList<>();
            addIfSet(attributter, "fom", overføring.getFom());
            addIfSet(attributter, "tom", overføring.getTom());
            attributter.add(txt("uttaksperiodetype", cap(overføring.getUttaksperiodeType().name())));
            attributter.add(txt("overføringsårsak", cap(overføring.getÅrsak().name())));
            return attributter;
        }
        if (periode instanceof GradertUttaksPeriode) {
            GradertUttaksPeriode gradert = GradertUttaksPeriode.class.cast(periode);
            ArrayList<String> attributter = new ArrayList<>();
            addIfSet(attributter, "fom", gradert.getFom());
            addIfSet(attributter, "tom", gradert.getTom());
            attributter.add(txt("uttaksperiodetype", cap(gradert.getUttaksperiodeType().name())));
            addIfSet(attributter, "virksomhetsnummer", gradert.getVirksomhetsnummer());
            attributter.add(txt("skalgraderes", jaNei(gradert.isArbeidsForholdSomskalGraderes())));
            attributter.add(txt("erarbeidstaker", jaNei(gradert.isErArbeidstaker())));
            addIfSet(attributter, gradert.getMorsAktivitetsType());
            if (antallBarn > 1) {
                attributter.add(txt("ønskerflerbarnsdager", jaNei(gradert.isØnskerFlerbarnsdager())));
            }
            attributter.add(txt("ønskersamtidiguttak", jaNei(gradert.isØnskerSamtidigUttak())));
            addIfSet(attributter, gradert.isØnskerSamtidigUttak(), "samtidiguttakprosent",
                    String.valueOf(gradert.getSamtidigUttakProsent()));
            return attributter;
        }
        if (periode instanceof UttaksPeriode) {
            UttaksPeriode uttak = UttaksPeriode.class.cast(periode);
            ArrayList<String> attributter = new ArrayList<>();
            addIfSet(attributter, "fom", uttak.getFom());
            addIfSet(attributter, "tom", uttak.getTom());
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
        if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode opphold = OppholdsPeriode.class.cast(periode);
            ArrayList<String> attributter = new ArrayList<>();
            addIfSet(attributter, "fom", opphold.getFom());
            addIfSet(attributter, "tom", opphold.getTom());
            attributter.add(txt("oppholdsårsak", cap(opphold.getÅrsak().name())));
            return attributter;
        }
        if (periode instanceof UtsettelsesPeriode) {
            UtsettelsesPeriode utsettelse = UtsettelsesPeriode.class.cast(periode);
            ArrayList<String> attributter = new ArrayList<>();
            addIfSet(attributter, "fom", utsettelse.getFom());
            addIfSet(attributter, "tom", utsettelse.getTom());
            attributter.add(txt("uttaksperiodetype", cap(utsettelse.getUttaksperiodeType().name())));
            attributter.add(txt("utsettelsesårsak", cap(utsettelse.getÅrsak().name())));
            addIfSet(attributter, "virksomhetsnummer", utsettelse.getVirksomhetsnummer());
            attributter.add(txt("erarbeidstaker", jaNei(utsettelse.isErArbeidstaker())));
            return attributter;
        }

        throw new IllegalArgumentException(periode.getClass().getSimpleName() + " ikke støttet");
    }

    private void addIfSet(List<String> attributter, MorsAktivitet morsAktivitetsType) {
        if (morsAktivitetsType != null) {
            attributter.add(txt("morsaktivitet", cap(morsAktivitetsType.name())));
        }

    }

    private String cap(String name) {
        return textFormatter.capitalize(name);
    }

    public float relasjonTilBarn(RelasjonTilBarnMedVedlegg relasjon, List<Vedlegg> vedlegg, PDPageContentStream cos,
            float y)
            throws IOException {
        y -= renderer.addBlankLine();
        y = omBarn(relasjon, cos, y);
        y = renderVedlegg(vedlegg, relasjon.getVedlegg(), "vedleggrelasjondok", cos, y);
        y -= renderer.addBlankLine();
        return y;
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
        List<String> attributter = new ArrayList<>();
        attributter.add(Optional.ofNullable(utenlandsForelder.getNavn())
                .map(n -> txt("navn", n))
                .orElse("Ukjent"));
        attributter.add(txt("nasjonalitet",
                textFormatter.countryName(utenlandsForelder.getLand().getAlpha2(),
                        utenlandsForelder.getLand().getName())));
        addIfSet(attributter, "utenlandskid", utenlandsForelder.getId());
        return attributter;
    }

    private List<String> norskForelder(NorskForelder norskForelder) {
        return Arrays.asList(
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
        addIfSet(attributter, ua.getLand());
        return attributter;
    }

    private List<List<String>> egneNæringer(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(this::egenNæring)
                .collect(toList());
    }

    private List<String> egenNæring(EgenNæring næring) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, næring.getArbeidsland());
        addIfSet(attributter, næring.getPeriode());
        attributter.add(txt("egennæringtyper", næring.getVedlegg().size() > 1 ? "r" : "",
                næring.getVirksomhetsTyper().stream()
                        .map(v -> textFormatter.capitalize(v.toString()))
                        .collect(joining(","))));
        if (næring.getPeriode().getTom() == null) {
            addIfSet(attributter, "egennæringpågår", textFormatter.date(næring.getPeriode().getFom()));
        }
        else {
            attributter.add(txt("egennæringavsluttet", næring.getPeriode().getFom(),
                    textFormatter.date(næring.getPeriode().getTom())));
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
            attributter.add(txt("regnskapsfører", rf.getNavn(), jaNei(næring.isNærRelasjon())));
        }

        if (næring instanceof NorskOrganisasjon) {
            NorskOrganisasjon org = NorskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "navn", org.getOrgName());
            addIfSet(attributter, "orgnummer", org.getOrgNummer());
        }
        else {
            UtenlandskOrganisasjon org = UtenlandskOrganisasjon.class.cast(næring);
            addIfSet(attributter, "navn", org.getOrgName());
        }
        return attributter;
    }

    private static Regnskapsfører regnskapsfører(EgenNæring næring) {
        if (næring.getRegnskapsførere().isEmpty()) {
            return null;
        }
        return næring.getRegnskapsførere().get(0);
    }

    private void addIfSet(List<String> attributter, String key, String value) {
        if (value != null) {
            attributter.add(txt(key, value));
        }
    }

    private void addIfSet(List<String> attributter, boolean value, String key, String otherValue) {
        if (value) {
            attributter.add(txt(key, otherValue));
        }
    }

    private void addIfSet(List<String> attributter, String key, LocalDate dato) {
        if (dato != null) {
            attributter.add(txt(key, textFormatter.date(dato)));
        }
    }

    private void addIfSet(List<String> attributter, String key, List<LocalDate> datoer) {
        if (!CollectionUtils.isEmpty(datoer)) {
            attributter.add(txt(key, textFormatter.dates(datoer)));
        }
    }

    private void addIfSet(List<String> attributter, String key, Optional<LocalDate> dato) {
        if (dato.isPresent()) {
            attributter.add(txt(key, textFormatter.date(dato.get())));
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

    private void addIfSet(List<String> attributter, CountryCode land) {
        if (land != null) {
            attributter.add(txt("land", textFormatter.countryName(land.getAlpha2())));
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
        addIfSet(attributter, "fødselsdato", adopsjon.getFødselsdato());
        return attributter;
    }

    private List<String> fødsel(Fødsel fødsel) {
        List<String> attributter = new ArrayList<>();
        addIfSet(attributter, "fødselsdato", fødsel.getFødselsdato());
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
