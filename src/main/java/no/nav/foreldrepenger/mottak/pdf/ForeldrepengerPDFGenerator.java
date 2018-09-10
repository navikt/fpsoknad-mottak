package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.capitalize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Component
public class ForeldrepengerPDFGenerator extends PDFGenerator {

    @Inject
    public ForeldrepengerPDFGenerator(MessageSource landkoder, MessageSource kvitteringstekster) {
        this(new SøknadInfoFormatter(landkoder, kvitteringstekster, CountryCode.NO.toLocale()));
    }

    private ForeldrepengerPDFGenerator(SøknadInfoFormatter infoFormatter) {
        super(infoFormatter);
    }

    public byte[] generate(Søknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        final PDPage page = newPage();
        try (PDDocument doc = new PDDocument();
                PDPageContentStream cos = new PDPageContentStream(doc, page);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            float y = PDFGenerator.calculateStartY();

            y -= header(søker, doc, cos, y);
            y -= addBlankLine();

            AnnenForelder annenForelder = stønad.getAnnenForelder();
            if (annenForelder != null) {
                y -= annenForelder(annenForelder, cos, y);
                String harRett = infoFormatter.fromMessageSource("harrett") +
                    infoFormatter.yesNo(stønad.getRettigheter().isHarAnnenForelderRett());
                y -= addLineOfRegularText(harRett, cos, y);
                y -= addBlankLine();
            }

            y -= dekningsgrad(stønad.getDekningsgrad(), cos, y);
            y -= addBlankLine();

            Opptjening opptjening = stønad.getOpptjening();
            if (opptjening != null) {
                y -= opptjening(opptjening, cos, y);
                y -= addBlankLine();
            }

            RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
            if (relasjon != null) {
                y -= omBarn(relasjon, cos, y);
                y -= addBlankLine();
            }

            Fordeling fordeling = stønad.getFordeling();
            if (fordeling != null) {
                y -= fordeling(fordeling, cos, y);
                y -= addBlankLine();
            }

            y -= addLeftHeading(infoFormatter.fromMessageSource("vedlegg"), cos, y);
            addBulletList(vedlegg(søknad.getVedlegg()), cos, y);

            doc.addPage(page);
            cos.close();
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    private float header(Person søker, PDDocument doc, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLogo(doc, cos, y);
        y -= addCenteredHeading(infoFormatter.fromMessageSource("søknad_fp"), cos, y);
        y -= addCenteredHeadings(søker(søker), cos, y);
        y -= addDividerLine(cos, y);
        return startY - y;
    }

    private List<String> søker(Person søker) {
        String fnr = søker.fnr.getFnr();
        String navn = infoFormatter.navn(søker);
        return Arrays.asList(fnr,
                navn != null ? navn : "ukjent");
    }

    private float annenForelder(AnnenForelder annenForelder, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("omfar"), cos, y);
        if (annenForelder instanceof NorskForelder) {
            y -= addLinesOfRegularText(norskForelder(annenForelder), cos, y);
        }
        else if (annenForelder instanceof UtenlandskForelder) {
            y -= addLinesOfRegularText(utenlandskForelder(annenForelder), cos, y);
        }
        else {
            y -= addLineOfRegularText("Ukjent", cos, y);
        }

        return startY - y;
    }

    private List<String> utenlandskForelder(AnnenForelder annenForelder) {
        UtenlandskForelder utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        List<String> lines = new ArrayList<>();
        lines.add(Optional.ofNullable(utenlandsForelder.getNavn())
            .map(n -> infoFormatter.fromMessageSource("navn", n))
            .orElse("Ukjent"));
        lines.add(infoFormatter.fromMessageSource("nasjonalitet",
                infoFormatter.countryName(utenlandsForelder.getLand().getAlpha2(),
                        utenlandsForelder.getLand().getName())));
        if (utenlandsForelder.getId() != null) {
            lines.add(infoFormatter.fromMessageSource("utenlandskid", utenlandsForelder.getId()));
        }
        return lines;
    }

    private List<String> norskForelder(AnnenForelder annenForelder) {
        NorskForelder norskForelder = NorskForelder.class.cast(annenForelder);
        return Arrays.asList(
            Optional.ofNullable(norskForelder.getNavn())
                .map(n -> infoFormatter.fromMessageSource("navn", n))
                .orElse("Ukjent"),
            infoFormatter.fromMessageSource("nasjonalitet", "Norsk"),
            infoFormatter.fromMessageSource("aktør", norskForelder.getFnr().getFnr())
        );
    }

    private float dekningsgrad(Dekningsgrad dekningsgrad, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("dekningsgrad"), cos, y);
        y -= addLineOfRegularText(dekningsgrad.kode() + "%", cos, y);
        return startY - y;
    }

    private float opptjening(Opptjening opptjening, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("infoominntekt"), cos, y);
        y -= addBulletList(utenlandskeArbeidsforhold(opptjening.getUtenlandskArbeidsforhold()), cos, y);
        if (!opptjening.getEgenNæring().isEmpty()) {
            y -= addBlankLine();
            y -= addLeftHeading(infoFormatter.fromMessageSource("egennæring"), cos, y);
            final List<List<String>> egneNæringer = egenNæring(opptjening.getEgenNæring());
            for (List<String> næring : egneNæringer) {
                y -= addLMultilineBulletpoint(næring, cos, y);
            }
        }
        if (!opptjening.getAnnenOpptjening().isEmpty()) {
            y -= addBlankLine();
            y -= addLeftHeading(infoFormatter.fromMessageSource("annenopptjening"), cos, y);
            y -= addBulletList(annenOpptjening(opptjening.getAnnenOpptjening()), cos, y);
        }
        return startY - y;
    }

    private float omBarn(RelasjonTilBarnMedVedlegg relasjon, PDPageContentStream cos, float y)
            throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("barn"), cos, y);
        y -= addLineOfRegularText(barn(relasjon), cos, y);
        return startY - y;
    }

    private float fordeling(Fordeling fordeling, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("perioder"), cos, y);
        y -= addBulletList(perioder(fordeling.getPerioder()), cos, y);
        y -= addBlankLine();
        String informert = infoFormatter.fromMessageSource("informert") +
            infoFormatter.yesNo(fordeling.isErAnnenForelderInformert());
        y -= addLineOfRegularText(informert, cos, y);
        return startY - y;
    }

    private List<String> utenlandskeArbeidsforhold(List<UtenlandskArbeidsforhold> arbeidsforhold) {
        return arbeidsforhold.stream()
                .map(this::format)
                .collect(toList());
    }

    private String format(UtenlandskArbeidsforhold arbeidsforhold) {
        UtenlandskArbeidsforhold ua = UtenlandskArbeidsforhold.class.cast(arbeidsforhold);
        return ua.getArbeidsgiverNavn() + " (" + infoFormatter.countryName(ua.getLand().getAlpha2()) + ")" + " - " +
                infoFormatter.periode(ua.getPeriode());
    }

    private List<List<String>> egenNæring(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(this::formatEgenNæring)
                .collect(toList());
    }

    private List<String> formatEgenNæring(EgenNæring næring) {
        CountryCode arbeidsland = Optional.ofNullable(næring.getArbeidsland()).orElse(CountryCode.NO);
        String typer = næring.getVirksomhetsTyper().stream()
            .map(v -> infoFormatter.capitalize(v.toString()))
            .collect(joining(","));
        StringBuilder sb = new StringBuilder(typer + " i " + infoFormatter.countryName(arbeidsland.getAlpha2()));
        sb.append(" hos ");
        if (næring instanceof NorskOrganisasjon) {
            NorskOrganisasjon org = NorskOrganisasjon.class.cast(næring);
            sb.append(org.getOrgName());
            sb.append(" (" + org.getOrgNummer() + ")");
        } else {
            UtenlandskOrganisasjon org = UtenlandskOrganisasjon.class.cast(næring);
            sb.append(org.getOrgName());
        }
        sb.append(" " + infoFormatter.periode(næring.getPeriode()));
        if (næring.isErVarigEndring()) {
            sb.append(", " + infoFormatter.fromMessageSource("varigendring"));
        }
        if (næring.isErNyOpprettet()) {
            sb.append(", " + infoFormatter.fromMessageSource("nyopprettet"));
        }
        if (næring.isErNyIArbeidslivet()) {
            sb.append(", " + infoFormatter.fromMessageSource("nyiarbeidslivet"));
        }
        sb.append("\n");

        sb.append("Brutto inntekt: " + næring.getNæringsinntektBrutto());
        sb.append(", regnskap: " + infoFormatter.navnToString(næring.getRegnskapsførere()));
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
        StringBuilder sb = new StringBuilder(infoFormatter.capitalize(annenOpptjening.getType().toString()));
        sb.append(" " + infoFormatter.periode(annenOpptjening.getPeriode()));
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
            return "Fødselsdato: " + infoFormatter.dato(fødsel.getFødselsdato());
        }
        else if (relasjonTilBarn instanceof Adopsjon) {
            Adopsjon adopsjon = Adopsjon.class.cast(relasjonTilBarn);
            return "Adopsjon: " + adopsjon.toString();
        }
        else if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel fødsel = FremtidigFødsel.class.cast(relasjonTilBarn);
            return "Fødsel med termin: " + infoFormatter.dato(fødsel.getTerminDato()) +
                " (bekreftelse utstedt " + infoFormatter.dato(fødsel.getUtstedtDato()) + ")";
        }
        else {
            Omsorgsovertakelse omsorgsovertakelse = Omsorgsovertakelse.class.cast(relasjonTilBarn);
            return "Omsorgsovertakelse: " + omsorgsovertakelse.getOmsorgsovertakelsesdato() +
                    ", " + omsorgsovertakelse.getÅrsak();
        }

    }

    private String format(LukketPeriodeMedVedlegg periode) {
        String tid = infoFormatter.dato(periode.getFom()) + " - " + infoFormatter.dato(periode.getTom());
        if (periode instanceof OverføringsPeriode) {
            OverføringsPeriode op = OverføringsPeriode.class.cast(periode);
            return "Overføring: " + tid + ", " + format(op.getÅrsak().name());
        }
        else if (periode instanceof UttaksPeriode) {
            UttaksPeriode up = UttaksPeriode.class.cast(periode);
            return "Uttak: " + tid + ", " + format(up.getUttaksperiodeType().name());
        }
        else if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode op = OppholdsPeriode.class.cast(periode);
            return "Opphold: " + tid + ", " + format(op.getÅrsak().name());
        }
        else {
            UtsettelsesPeriode up = UtsettelsesPeriode.class.cast(periode);
            return "Utsettelse: " + tid + ", " + format(up.getÅrsak().name());
        }
    }

    private static String format(String name) {
        return name != null ? capitalize(Joiner.on(' ').join(Splitter.on("_").split(name)).toLowerCase()) : "";
    }

    private List<String> vedlegg(List<Vedlegg> vedlegg) {
        return vedlegg.stream()
                .map(this::format)
                .collect(toList());
    }

    private String format(Vedlegg vedlegg) {
        return Optional.ofNullable(vedlegg.getMetadata().getBeskrivelse()).orElse(vedlegg.getDokumentType().beskrivelse);
    }

}
