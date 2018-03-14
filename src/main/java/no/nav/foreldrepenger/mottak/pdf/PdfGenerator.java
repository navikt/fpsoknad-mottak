package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.joining;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.Utenlandsopphold;

@Service
public class PdfGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    private static final Font HEADING = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

    private static final Locale BOKMÅL = CountryCode.NO.toLocale();

    private final MessageSource landkoder;
    private final MessageSource kvitteringstekster;

    @Inject
    public PdfGenerator(@Qualifier("landkoder") MessageSource landkoder,
            @Qualifier("kvitteringstekster") MessageSource kvitteringstekster) {
        this.landkoder = Objects.requireNonNull(landkoder);
        this.kvitteringstekster = Objects.requireNonNull(kvitteringstekster);
    }

    public byte[] generate(Søknad søknad) {

        try {
            Engangsstønad stønad = Engangsstønad.class.cast(søknad.getYtelse());
            Medlemsskap medlemsskap = stønad.getMedlemsskap();
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            Image logo = logo();
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);

            document.add(centeredParagraph(getMessage("søknad", kvitteringstekster), HEADING));
            document.add(centeredParagraph(søknad.getSøker().getFnr().getFnr(), NORMAL));
            String navn = navn(søknad.getSøker().getNavn());
            if (!navn.isEmpty()) {
                document.add(centeredParagraph(navn, NORMAL));
            }
            document.add(separator());

            document.add(blankLine());

            document.add(paragraph(getMessage("ombarn", kvitteringstekster), HEADING));
            document.add(paragraph(
                    getMessage("gjelder", kvitteringstekster, stønad.getRelasjonTilBarn().getAntallBarn()),
                    NORMAL));
            if (erFremtidigFødsel(stønad)) {
                FremtidigFødsel ff = FremtidigFødsel.class.cast(stønad.getRelasjonTilBarn());
                document.add(
                        paragraph(getMessage("termindato", kvitteringstekster, dato(ff.getTerminDato())), NORMAL));
                if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
                    document.add(paragraph(
                            getMessage("termindatotekst", kvitteringstekster, dato(ff.getUtstedtDato())), NORMAL));
                }
            }

            document.add(blankLine());
            document.add(paragraph(getMessage("tilknytning", kvitteringstekster), HEADING));
            document.add(paragraph(getMessage("siste12", kvitteringstekster), NORMAL));
            document.add(
                    paragraph(formatOpphold(medlemsskap.getTidligereOppholdsInfo().getUtenlandsOpphold()), NORMAL));
            document.add(paragraph(getMessage("neste12", kvitteringstekster,
                    formatOpphold(medlemsskap.getFramtidigOppholdsInfo().getUtenlandsOpphold())), NORMAL));
            if (erFremtidigFødsel(stønad)) {
                document.add(paragraph(getMessage("føde", kvitteringstekster,
                        countryName(medlemsskap.getFramtidigOppholdsInfo().isFødselNorge())), NORMAL));
            }

            document.add(blankLine());

            document.add(paragraph(getMessage("tillegg", kvitteringstekster), HEADING));
            document.add(
                    paragraph(Optional.ofNullable(søknad.getTilleggsopplysninger()).orElse("Ingen"), NORMAL));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Image logo() throws BadElementException, MalformedURLException, IOException {
        return Image.getInstance(
                StreamUtils.copyToByteArray(new ClassPathResource("pdf/nav-logo.png").getInputStream()));
    }

    private String navn(Navn søker) {
        return (Optional.ofNullable(søker.getFornavn()).orElse("") + " "
                + Optional.ofNullable(søker.getMellomnavn()).orElse("") + " "
                + Optional.ofNullable(søker.getEtternavn()).orElse("")).trim();
    }

    private boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof FremtidigFødsel;
    }

    private String dato(LocalDate dato) {
        return dato.format(DATE_FMT);
    }

    private static String countryName(Boolean b) {
        return b ? "Norge" : "utlandet";
    }

    private Paragraph paragraph(String txt, Font font) {
        return new Paragraph(new Chunk(txt, font));
    }

    private Paragraph centeredParagraph(String txt, Font font) {
        Paragraph p = paragraph(txt, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private Element separator() {
        Paragraph p = new Paragraph();
        DottedLineSeparator dottedline = new DottedLineSeparator();
        dottedline.setGap(2f);
        dottedline.setOffset(2);
        p.add(dottedline);
        p.add(blankLine());
        return p;
    }

    private Element blankLine() {
        Paragraph p = new Paragraph();
        p.add(Chunk.NEWLINE);
        return p;
    }

    private String formatOpphold(List<Utenlandsopphold> opphold) {
        if (opphold.isEmpty()) {
            return getMessage(CountryCode.NO.getAlpha2(), landkoder);
        }
        return opphold.stream()
                .map(this::formatOpphold)
                .collect(joining("\n"));
    }

    private String formatOpphold(Utenlandsopphold opphold) {

        return getMessage(opphold.getLand().getAlpha2(), opphold.getLand().getName(), landkoder)
                + ": "
                + opphold.getVarighet().getFom().format(DATE_FMT) + " - "
                +
                opphold.getVarighet().getTom().format(DATE_FMT);
    }

    private String getMessage(String key, MessageSource messages, Object... values) {
        return getMessage(key, null, messages, values);
    }

    private String getMessage(String key, String defaultValue, MessageSource messages, Object... values) {
        return messages.getMessage(key, values, defaultValue, BOKMÅL);
    }

}
