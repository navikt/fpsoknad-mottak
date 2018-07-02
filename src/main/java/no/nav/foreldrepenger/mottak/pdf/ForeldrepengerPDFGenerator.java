package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fødsel;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.UttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype;

@Component
public class ForeldrepengerPDFGenerator extends AbstractPDFGenerator {

    public ForeldrepengerPDFGenerator(MessageSource landkoder, MessageSource kvitteringstekster) {
        super(landkoder, kvitteringstekster, CountryCode.NO.toLocale());
    }

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPDFGenerator.class);

    public byte[] generate(Søknad søknad, Person søker) {
        try {
            Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            logo(document);

            document.add(center(heading(fromMessageSource("søknad_fp"))));
            document.add(søker(søker));
            if (stønad.getOpptjening() != null) {
                document.add(arbeidsforhold(stønad.getOpptjening().getArbeidsforhold()));
                document.add(blankLine());
                document.add(egenNæring(stønad.getOpptjening().getEgenNæring()));
            }
            if (stønad.getRelasjonTilBarn() != null) {
                document.add(blankLine());
                document.add(barn(stønad.getRelasjonTilBarn()));
                document.add(blankLine());
            }
            document.add(annenForelder(stønad));
            document.add(blankLine());
            document.add(dekningsgrad(stønad.getDekningsgrad()));
            if (stønad.getFordeling() != null) {
                document.add(blankLine());
                document.add(perioder(stønad.getFordeling().getPerioder()));
                document.add(blankLine());
            }
            document.add(vedlegg(søknad.getVedlegg()));
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            LOG.warn("Noe gikk feil, gitt", e);
            throw new IllegalArgumentException(e);
        }
    }

    private static Element søker(Person søker) {
        Paragraph p = new Paragraph();

        p.add(center(regularParagraph(søker.fnr.getFnr())));
        String navn = navn(søker);
        if (navn != null &&
                !navn.isEmpty()) {
            p.add(center(regularParagraph(navn)));
        }

        p.add(separator());
        p.add(blankLine());
        return p;
    }

    private static String navn(Person søker) {
        if (søker == null) {
            return null;
        }
        return (Optional.ofNullable(søker.fornavn).orElse("") + " "
                + Optional.ofNullable(søker.mellomnavn).orElse("") + " "
                + Optional.ofNullable(søker.etternavn).orElse("")).trim();
    }

    private Paragraph annenForelder(Foreldrepenger stønad) {
        AnnenForelder annenForelder = stønad.getAnnenForelder();
        Paragraph paragraph = new Paragraph();

        if (annenForelder != null) {
            paragraph.add(heading(fromMessageSource("omfar")));

            if (annenForelder instanceof NorskForelder) {
                paragraph.add(norskForelder(annenForelder));
            }
            else if (annenForelder instanceof UtenlandskForelder) {
                paragraph.add(utenlandskForelder(annenForelder));
            }
            else {
                paragraph.add(regularParagraph("Ukjent"));
            }
        }

        return paragraph;
    }

    private Paragraph utenlandskForelder(AnnenForelder annenForelder) {
        UtenlandskForelder utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        Paragraph paragraph = new Paragraph();
        paragraph.add(regularParagraph(fromMessageSource("nasjonalitet",
                countryName(utenlandsForelder.getLand().getAlpha2(), utenlandsForelder.getLand().getName()))));
        if (utenlandsForelder.getId() != null) {
            paragraph.add(regularParagraph(fromMessageSource("utenlandskid", utenlandsForelder.getId())));
        }
        return paragraph;
    }

    private Paragraph norskForelder(AnnenForelder annenForelder) {
        NorskForelder norskForelder = NorskForelder.class.cast(annenForelder);
        Paragraph paragraph = new Paragraph();
        paragraph.add(regularParagraph(fromMessageSource("nasjonalitet", "Norsk")));
        paragraph.add(regularParagraph(fromMessageSource("aktør", norskForelder.getAktørId().getId())));
        return paragraph;
    }

    private Paragraph arbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(heading(fromMessageSource("arbeidsforhold")));
        final List<String> formatted = arbeidsforhold.stream()
                .map(this::format)
                .collect(toList());
        paragraph.add(bulletedList(formatted));
        return paragraph;
    }

    private Paragraph egenNæring(List<EgenNæring> egenNæring) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(heading(fromMessageSource("egennæring")));
        final List<String> formatted = egenNæring.stream()
                .map(this::formatEgenNæring)
                .collect(toList());
        paragraph.add(bulletedList(formatted));
        return paragraph;
    }

    private String format(Arbeidsforhold arbeidsforhold) {
        UtenlandskArbeidsforhold ua = UtenlandskArbeidsforhold.class.cast(arbeidsforhold);
        return ua.getArbeidsgiverNavn() + " (" + countryName(ua.getLand().getAlpha2()) + ")" + "\n" +
                dato(ua.getPeriode().getFom()) + "\n" +
                ua.getBeskrivelseRelasjon();
    }

    private String formatEgenNæring(EgenNæring næring) {
        return næring.getVirksomhetsTyper().stream()
                .map(s -> formatVirksomhetsType(s, næring))
                .collect(Collectors.joining("\n\n"));
    }

    private String formatVirksomhetsType(Virksomhetstype type, EgenNæring næring) {
        return type.name() + " (" + countryName(næring.getArbeidsland().getAlpha2()) + ")" + "\n" +
                dato(næring.getPeriode().getFom()) + "\n" +
                næring.isNærRelasjon() + "\n" +
                navnToString(næring.getRegnskapsfører().getNavn());
    }

    private Paragraph dekningsgrad(Dekningsgrad dekningsgrad) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(heading(fromMessageSource("dekningsgrad")));
        paragraph.add(regularParagraph(dekningsgrad.kode() + "%"));
        return paragraph;
    }

    private Paragraph perioder(List<LukketPeriodeMedVedlegg> perioder) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(heading(fromMessageSource("perioder")));
        final List<String> formatted = perioder.stream()
                .map(this::format)
                .collect(toList());
        paragraph.add(bulletedList(formatted));
        return paragraph;
    }

    private Paragraph barn(RelasjonTilBarnMedVedlegg relasjonTilBarn) {
        if (relasjonTilBarn instanceof Fødsel) {
            Fødsel fødsel = Fødsel.class.cast(relasjonTilBarn);
            return barn("Fødselsdato: " + dato(fødsel.getFødselsdato()));
        }
        if (relasjonTilBarn instanceof Adopsjon) {
            Adopsjon adopsjon = Adopsjon.class.cast(relasjonTilBarn);
            return barn("Adopsjon: " + adopsjon.toString());
        }
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel fødsel = FremtidigFødsel.class.cast(relasjonTilBarn);
            return barn("Fødsel med termin: " + dato(fødsel.getTerminDato()));
        }

        Omsorgsovertakelse omsorgsovertakelse = Omsorgsovertakelse.class.cast(relasjonTilBarn);
        return barn("Omsorgsovertakelse: " + omsorgsovertakelse.getOmsorgsovertakelsesdato() +
                ", " + omsorgsovertakelse.getÅrsak());

    }

    private Paragraph barn(String txt) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(heading(fromMessageSource("barn")));
        paragraph.add(regularParagraph(txt));
        return paragraph;
    }

    private String format(LukketPeriodeMedVedlegg periode) {
        String tid = dato(periode.getFom()) + " - " + dato(periode.getTom());
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

    private Paragraph vedlegg(List<Vedlegg> vedlegg) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(heading(fromMessageSource("vedlegg")));
        final List<String> formatted = vedlegg.stream()
                .map(this::format)
                .collect(toList());
        paragraph.add(bulletedList(formatted));
        return paragraph;
    }

    private String format(Vedlegg vedlegg) {
        return vedlegg.getMetadata().getSkjemanummer().beskrivelse;
    }

}
