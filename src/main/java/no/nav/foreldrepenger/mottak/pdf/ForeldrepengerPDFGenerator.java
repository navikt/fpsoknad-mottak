package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype;

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
                y -= relasjonTilBarn(relasjon, cos, y);
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
        return Arrays.asList(infoFormatter.fromMessageSource("nasjonalitet", "Norsk"),
                infoFormatter.fromMessageSource("aktør", norskForelder.getAktørId().getId()));
    }

    private float dekningsgrad(Dekningsgrad dekningsgrad, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("dekningsgrad"), cos, y);
        y -= addLineOfRegularText(dekningsgrad.kode() + "%", cos, y);
        return startY - y;
    }

    private float opptjening(Opptjening opptjening, PDPageContentStream cos, float y) throws IOException {
        float startY = y;
        y -= addLeftHeading(infoFormatter.fromMessageSource("arbeidsforhold"), cos, y);
        y -= addBulletList(utenlandskeArbeidsforhold(opptjening.getUtenlandskArbeidsforhold()), cos, y);
        y -= addBlankLine();
        y -= addLeftHeading(infoFormatter.fromMessageSource("egennæring"), cos, y);
        y -= addBulletList(egenNæring(opptjening.getEgenNæring()), cos, y);
        return startY - y;
    }

    private float relasjonTilBarn(RelasjonTilBarnMedVedlegg relasjon, PDPageContentStream cos, float y)
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
                "fom. " + infoFormatter.dato(ua.getPeriode().getFom());
    }

    private List<String> egenNæring(List<EgenNæring> egenNæring) {
        return egenNæring.stream()
                .map(this::formatEgenNæring)
                .collect(toList());
    }

    private String formatEgenNæring(EgenNæring næring) {
        return næring.getVirksomhetsTyper().stream()
                .map(s -> formatVirksomhetsType(s, næring))
                .collect(Collectors.joining(" - "));
    }

    private String formatVirksomhetsType(Virksomhetstype type, EgenNæring næring) {
        CountryCode arbeidsland = Optional.ofNullable(næring.getArbeidsland()).orElse(CountryCode.NO);
        return type.name() + " (" + infoFormatter.countryName(arbeidsland.getAlpha2()) + ")" + " - " +
                "fom." + infoFormatter.dato(næring.getPeriode().getFom()) + " - " +
                "regnskapsfører " + infoFormatter.navnToString(næring.getRegnskapsførere());
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
            return "Fødsel med termin: " + infoFormatter.dato(fødsel.getTerminDato());
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
            return "Overføring: " + tid + ", " + op.getÅrsak();
        }
        else if (periode instanceof UttaksPeriode) {
            UttaksPeriode up = UttaksPeriode.class.cast(periode);
            return "Uttak: " + tid + ", " + up.getUttaksperiodeType();
        }
        else if (periode instanceof OppholdsPeriode) {
            OppholdsPeriode op = OppholdsPeriode.class.cast(periode);
            return "Opphold: " + tid + ", " + op.getÅrsak();
        }
        else {
            UtsettelsesPeriode up = UtsettelsesPeriode.class.cast(periode);
            return "Utsettelse: " + tid + ", " + up.getÅrsak();
        }
    }

    private List<String> vedlegg(List<Vedlegg> vedlegg) {
        return vedlegg.stream()
                .map(this::format)
                .collect(toList());
    }

    private String format(Vedlegg vedlegg) {
        return vedlegg.getMetadata().getSkjemanummer().beskrivelse;
    }

}
