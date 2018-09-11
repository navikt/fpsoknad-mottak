package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import no.nav.foreldrepenger.mottak.domain.felles.*;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.*;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Omsorgsovertakelse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Søknad;

@Component
public class ForeldrepengerPDFGenerator {
    private PDFElementRenderer renderer;
    private SøknadTextFormatter textFormatter;

    @Inject
    public ForeldrepengerPDFGenerator(MessageSource landkoder, MessageSource kvitteringstekster) {
        this(new SøknadTextFormatter(landkoder, kvitteringstekster, CountryCode.NO.toLocale()));
    }

    private ForeldrepengerPDFGenerator(SøknadTextFormatter textFormatter) {
        this.textFormatter = textFormatter;
        this.renderer = new PDFElementRenderer();
    }

    public byte[] generate(Søknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());

        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument();
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page1 = renderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page1)) {
                float y = yTop;
                y -= header(søker, doc, cos, y);
                y -= renderer.addBlankLine();

                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y -= annenForelder(annenForelder, stønad.getRettigheter().isHarAnnenForelderRett(), cos, y);
                    y -= renderer.addBlankLine();
                }

                y -= dekningsgrad(stønad.getDekningsgrad(), cos, y);


                Opptjening opptjening = stønad.getOpptjening();
                if (opptjening != null) {
                    opptjening(opptjening, cos, y);
                }

                doc.addPage(page1);
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

            PDPage page2 = renderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page2)) {
                float y = yTop;
                y -= header(søker, doc, cos, y);
                y -= renderer.addBlankLine();

                y -= medlemsskap(stønad.getMedlemsskap(), cos, y);

                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= renderer.addBlankLine();
                    y -= omBarn(relasjon, cos, y);
                    y -= renderer.addBlankLine();
                }

                Fordeling fordeling = stønad.getFordeling();
                if (fordeling != null) {
                    y -= fordeling(fordeling, cos, y);
                    y -= renderer.addBlankLine();
                }

                y -= renderer.addLeftHeading(textFormatter.fromMessageSource("vedlegg"), cos, y);
                renderer.addBulletList(vedlegg(søknad.getVedlegg()), cos, y);

                doc.addPage(page2);
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }

    }

    private float header(Person søker, PDDocument doc, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLogo(doc, cos, y);
        y -= renderer.addCenteredHeading(textFormatter.fromMessageSource("søknad_fp"), cos, y);
        y -= renderer.addCenteredHeadings(søker(søker), cos, y);
        y -= renderer.addDividerLine(cos, y);
        return startY - y;
    }

    private List<String> søker(Person søker) {
        String fnr = søker.fnr.getFnr();
        String navn = textFormatter.navn(søker);
        return Arrays.asList(fnr,
                navn != null ? navn : "ukjent");
    }

    private float annenForelder(AnnenForelder annenForelder, boolean harAnnenForelderRett,
                                PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("omfar"), cos, y);
        if (annenForelder instanceof NorskForelder) {
            y -= renderer.addLinesOfRegularText(norskForelder(annenForelder), cos, y);
        }
        else if (annenForelder instanceof UtenlandskForelder) {
            y -= renderer.addLinesOfRegularText(utenlandskForelder(annenForelder), cos, y);
        }
        else {
            y -= renderer.addLineOfRegularText("Ukjent", cos, y);
        }

        String harRett = textFormatter.fromMessageSource("harrett") +
            textFormatter.yesNo(harAnnenForelderRett);
        y -= renderer.addLineOfRegularText(harRett, cos, y);

        return startY - y;
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

    private List<String> norskForelder(AnnenForelder annenForelder) {
        NorskForelder norskForelder = NorskForelder.class.cast(annenForelder);
        return Arrays.asList(
            Optional.ofNullable(norskForelder.getNavn())
                .map(n -> textFormatter.fromMessageSource("navn", n))
                .orElse("Ukjent"),
            textFormatter.fromMessageSource("nasjonalitet", "Norsk"),
            textFormatter.fromMessageSource("aktør", norskForelder.getFnr().getFnr())
        );
    }

    private float dekningsgrad(Dekningsgrad dekningsgrad, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("dekningsgrad"), cos, y);
        y -= renderer.addLineOfRegularText(dekningsgrad.kode() + "%", cos, y);
        return startY - y;
    }

    private float opptjening(Opptjening opptjening, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addBlankLine();
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("infoominntekt"), cos, y);
        y -= renderer.addBulletList(utenlandskeArbeidsforhold(opptjening.getUtenlandskArbeidsforhold()), cos, y);
        if (!opptjening.getEgenNæring().isEmpty()) {
            y -= renderer.addBlankLine();
            y -= renderer.addLeftHeading(textFormatter.fromMessageSource("egennæring"), cos, y);
            final List<List<String>> egneNæringer = egenNæring(opptjening.getEgenNæring());
            for (List<String> næring : egneNæringer) {
                y -= renderer.addLMultilineBulletpoint(næring, cos, y);
            }
        }
        if (!opptjening.getAnnenOpptjening().isEmpty()) {
            y -= renderer.addBlankLine();
            y -= renderer.addLeftHeading(textFormatter.fromMessageSource("annenopptjening"), cos, y);
            y -= renderer.addBulletList(annenOpptjening(opptjening.getAnnenOpptjening()), cos, y);
        }
        if (opptjening.getFrilans() != null) {
            y -= frilans(opptjening.getFrilans(), cos, y);
        }
        return startY - y;
    }

    private float frilans(Frilans frilans, PDPageContentStream cos, float y) throws IOException {
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
        y -= renderer.addLineOfRegularText(sb.toString(), cos, y);
        List<String> oppdrag = frilans.getFrilansOppdrag().stream()
            .map(o -> o.getOppdragsgiver() + " " + textFormatter.periode(o.getPeriode()))
            .collect(toList());
        y -= renderer.addBulletList(oppdrag, cos, y);
        return startY - y;
    }

    private float medlemsskap(Medlemsskap medlemsskap, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("medlemsskap"), cos, y);
        TidligereOppholdsInformasjon tidligereOpphold = medlemsskap.getTidligereOppholdsInfo();
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("siste12") + " " +
            (tidligereOpphold.isBoddINorge() ? "Norge" : "utlandet"), cos, y);
        if (tidligereOpphold.getUtenlandsOpphold().size() != 0) {
            y -= renderer.addLeftHeading(textFormatter.fromMessageSource("tidligereopphold"), cos, y);
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(tidligereOpphold.getUtenlandsOpphold()), cos, y);
        }
        FramtidigOppholdsInformasjon framtidigeOpphold = medlemsskap.getFramtidigOppholdsInfo();
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("neste12") + " " +
            (framtidigeOpphold.isNorgeNeste12() ? "Norge" : "utlandet"), cos, y);
        y -= renderer.addLineOfRegularText(textFormatter.fromMessageSource("føderi",
            (framtidigeOpphold.isFødselNorge() ? "Norge" : "utlandet")), cos, y);
        if (framtidigeOpphold.getUtenlandsOpphold().size() != 0) {
            y -= renderer.addLeftHeading(textFormatter.fromMessageSource("framtidigeopphold"), cos, y);
            y -= renderer.addBulletList(textFormatter.utenlandsOpphold(framtidigeOpphold.getUtenlandsOpphold()), cos, y);
        }
        return startY - y;
    }

    private float omBarn(RelasjonTilBarnMedVedlegg relasjon, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -=  renderer.addLeftHeading(textFormatter.fromMessageSource("barn"), cos, y);
        y -= renderer.addLineOfRegularText(barn(relasjon), cos, y);
        return startY - y;
    }

    private float fordeling(Fordeling fordeling, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= renderer.addLeftHeading(textFormatter.fromMessageSource("perioder"), cos, y);
        y -= renderer.addBulletList(perioder(fordeling.getPerioder()), cos, y);
        y -= renderer.addBlankLine();
        String informert = textFormatter.fromMessageSource("informert") +
            textFormatter.yesNo(fordeling.isErAnnenForelderInformert());
        y -= renderer.addLineOfRegularText(informert, cos, y);
        return startY - y;
    }

    private List<String> utenlandskeArbeidsforhold(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        return arbeidsforhold.stream()
                .map(this::format)
                .collect(toList());
    }

    private String format(UtenlandskArbeidsforhold arbeidsforhold) {
        UtenlandskArbeidsforhold ua = UtenlandskArbeidsforhold.class.cast(arbeidsforhold);
        return ua.getArbeidsgiverNavn() + " (" + textFormatter.countryName(ua.getLand().getAlpha2()) + ")" + " - " +
                textFormatter.periode(ua.getPeriode());
    }

    private List<List<String>> egenNæring(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(this::formatEgenNæring)
                .collect(toList());
    }

    private List<String> formatEgenNæring(EgenNæring næring) {
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
        } else {
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
        sb.append(", regnskap: " + textFormatter.regnskapsførere(næring.getRegnskapsførere()));
        sb.append("\n");

        sb.append(næring.getBeskrivelseEndring());

        return Arrays.asList(sb.toString().split("\n"));
    }

    private List<String> annenOpptjening(List<AnnenOpptjening> annenOpptjening) {
        return annenOpptjening.stream()
            .map(this::formatAnnenOpptjening)
            .collect(toList());
    }

    private String formatAnnenOpptjening(AnnenOpptjening annenOpptjening) {
        StringBuilder sb = new StringBuilder(textFormatter.capitalize(annenOpptjening.getType().toString()));
        sb.append(" " + textFormatter.periode(annenOpptjening.getPeriode()));
        if (annenOpptjening.getVedlegg().size() != 0) {
            sb.append(", vedlegg: " + annenOpptjening.getVedlegg().stream().collect(joining(", ")));
        }
        return sb.toString();
    }

    private List<String> perioder(List<LukketPeriodeMedVedlegg> perioder) {
        return perioder.stream()
                .map(this::format)
                .collect(toList());
    }

    private String barn(RelasjonTilBarnMedVedlegg relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel) {
            Fødsel fødsel = Fødsel.class.cast(relasjonTilBarn);
            return "Fødselsdato: " + textFormatter.dates(fødsel.getFødselsdato());
        }
        else if (relasjonTilBarn instanceof Adopsjon) {
            Adopsjon adopsjon = Adopsjon.class.cast(relasjonTilBarn);
            return "Adopsjon: " + adopsjon.toString();
        }
        else if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel fødsel = FremtidigFødsel.class.cast(relasjonTilBarn);
            return "Fødsel med termin: " + textFormatter.date(fødsel.getTerminDato()) +
                " (bekreftelse utstedt " + textFormatter.date(fødsel.getUtstedtDato()) + ")";
        }
        else {
            Omsorgsovertakelse omsorgsovertakelse = Omsorgsovertakelse.class.cast(relasjonTilBarn);
            return "Omsorgsovertakelse: " + omsorgsovertakelse.getOmsorgsovertakelsesdato() +
                    ", " + omsorgsovertakelse.getÅrsak();
        }

    }

    private String format(LukketPeriodeMedVedlegg periode) {
        String tid = textFormatter.date(periode.getFom()) + " - " + textFormatter.date(periode.getTom());
        if (periode instanceof OverføringsPeriode) {
            OverføringsPeriode overføring = OverføringsPeriode.class.cast(periode);
            return "Overføring: " + tid + ", " + textFormatter.capitalize(overføring.getÅrsak().name());
        }
        else if (periode instanceof UttaksPeriode) {
            UttaksPeriode uttak = UttaksPeriode.class.cast(periode);
            return "Uttak: " + tid + ", " + textFormatter.capitalize(uttak.getUttaksperiodeType().name());
        }
        else if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode opphold = OppholdsPeriode.class.cast(periode);
            return "Opphold: " + tid + ", " + textFormatter.capitalize(opphold.getÅrsak().name());
        }
        else {
            UtsettelsesPeriode utsettelse = UtsettelsesPeriode.class.cast(periode);
            return "Utsettelse: " + tid + ", " + textFormatter.capitalize(utsettelse.getÅrsak().name());
        }
    }

    private List<String> vedlegg(List<Vedlegg> vedlegg) {
        return vedlegg.stream()
                .map(v -> textFormatter.vedlegg(v))
                .collect(toList());
    }

}
