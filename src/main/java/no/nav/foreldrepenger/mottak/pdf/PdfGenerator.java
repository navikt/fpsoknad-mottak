package no.nav.foreldrepenger.mottak.pdf;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.Fødsel;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
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
        this.landkoder = requireNonNull(landkoder);
        this.kvitteringstekster = requireNonNull(kvitteringstekster);
    }

    public byte[] generate(Søknad søknad) {

        try {
            Engangsstønad stønad = Engangsstønad.class.cast(søknad.getYtelse());
            Medlemsskap medlemsskap = stønad.getMedlemsskap();
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            logo(document);
            overskrift(document);
            søker(søknad, document);
            omBarn(stønad, document);
            if (erFremtidigFødsel(stønad)) {
                fødsel(søknad, stønad, document);
            }
            if (erFødt(stønad)) {
                født(søknad, stønad, document);
            }
            blankLine(document);
            medlemsskap(medlemsskap, document);
            if (erFremtidigFødsel(stønad)) {
                fødselssted(medlemsskap, document);
            }

            blankLine(document);
            omFar(stønad, document);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void født(Søknad søknad, Engangsstønad stønad, Document document) throws DocumentException {
        Fødsel ff = Fødsel.class.cast(stønad.getRelasjonTilBarn());
        document.add(
                paragraph(getMessage("fødselsdato", kvitteringstekster, dato(ff.getFødselsdato())),
                        NORMAL));
    }

    private void omFar(Engangsstønad stønad, Document document) throws DocumentException {
        document.add(paragraph(getMessage("omfar", kvitteringstekster), HEADING));
        AnnenForelder annenForelder = stønad.getAnnenForelder();
        if (annenForelder != null) {
            if (annenForelder instanceof NorskForelder) {
                norskForelder(document, annenForelder);
            }
            if (annenForelder instanceof UtenlandskForelder) {
                utenlandskForelder(document, annenForelder);
            }
            if (annenForelder instanceof UkjentForelder) {
                document.add(paragraph("Ukjent", NORMAL));
            }
            blankLine(document);
        }
    }

    private void utenlandskForelder(Document document, AnnenForelder annenForelder) throws DocumentException {
        UtenlandskForelder utenlandsForelder = UtenlandskForelder.class.cast(annenForelder);
        document.add(paragraph(getMessage("nasjonalitet", kvitteringstekster, utenlandsForelder.getLand().getAlpha2()),
                NORMAL));
        navn(document, utenlandsForelder.getNavn(), NORMAL);
        if (utenlandsForelder.getId() != null) {
            document.add(paragraph(getMessage("utenlandskid", kvitteringstekster, utenlandsForelder.getId()), NORMAL));
        }
    }

    private void norskForelder(Document document, AnnenForelder annenForelder) throws DocumentException {
        NorskForelder norskForelder = NorskForelder.class.cast(annenForelder);
        document.add(paragraph(getMessage("nasjonalitet", kvitteringstekster, "Norsk"), NORMAL));
        navn(document, norskForelder.getNavn(), NORMAL);
        document.add(paragraph(getMessage("fødselsnummer", kvitteringstekster, norskForelder.getFnr().getFnr()),
                NORMAL));
    }

    private void fødselssted(Medlemsskap medlemsskap, Document document) throws DocumentException {
        document.add(paragraph(getMessage("føde", kvitteringstekster,
                countryName(medlemsskap.getFramtidigOppholdsInfo().isFødselNorge())), NORMAL));
    }

    private void medlemsskap(Medlemsskap medlemsskap, Document document) throws DocumentException {
        document.add(paragraph(getMessage("tilknytning", kvitteringstekster), HEADING));
        List<String> siste12 = utenlandsOpphold(medlemsskap.getTidligereOppholdsInfo().getUtenlandsOpphold());
        if (siste12.size() == 1) {
            document.add(paragraph(getMessage("siste12solo", kvitteringstekster, siste12.get(0)), NORMAL));
        }
        else {
            document.add(paragraph(getMessage("siste12", kvitteringstekster), NORMAL));
            document.add(bulletedList(siste12));
        }
        List<String> neste12 = utenlandsOpphold(medlemsskap.getFramtidigOppholdsInfo().getUtenlandsOpphold());
        if (neste12.size() == 1) {
            document.add(paragraph(getMessage("neste12solo", kvitteringstekster, neste12.get(0)), NORMAL));
        }
        else {
            document.add(paragraph(getMessage("neste12", kvitteringstekster), NORMAL));
            document.add(bulletedList(neste12));
        }
    }

    private static com.itextpdf.text.List bulletedList(List<String> oppholdsInfo) {
        com.itextpdf.text.List bulletedList = new com.itextpdf.text.List();
        bulletedList.setListSymbol(new Chunk("\u2022", new Font()));
        bulletedList.setSymbolIndent(12);
        oppholdsInfo.stream().forEach(s -> bulletedList.add(s));
        return bulletedList;
    }

    private void omBarn(Engangsstønad stønad, Document document) throws DocumentException {
        document.add(paragraph(getMessage("ombarn", kvitteringstekster), HEADING));
        document.add(paragraph(
                getMessage("gjelder", kvitteringstekster, stønad.getRelasjonTilBarn().getAntallBarn()),
                NORMAL));
    }

    private void overskrift(Document document) throws DocumentException {
        document.add(centeredParagraph(getMessage("søknad", kvitteringstekster), HEADING));
    }

    private void logo(Document document)
            throws BadElementException, MalformedURLException, IOException, DocumentException {
        Image logo = logo();
        logo.setAlignment(Image.ALIGN_CENTER);
        document.add(logo);
    }

    private void søker(Søknad søknad, Document document) throws DocumentException {
        document.add(centeredParagraph(søknad.getSøker().getFnr().getFnr(), NORMAL));
        String navn = navn(søknad.getSøker().getNavn());
        if (!navn.isEmpty()) {
            document.add(centeredParagraph(navn, NORMAL));
        }
        document.add(separator());
        blankLine(document);
    }

    private void navn(Document document, Navn navn, Font font) throws DocumentException {
        String n = navn(navn);
        if (!n.isEmpty()) {
            document.add(paragraph(getMessage("navn", kvitteringstekster, n), NORMAL));
        }
    }

    private void fødsel(Søknad søknad, Engangsstønad stønad, Document document) throws DocumentException {
        FremtidigFødsel ff = FremtidigFødsel.class.cast(stønad.getRelasjonTilBarn());
        document.add(
                paragraph(getMessage("termindato", kvitteringstekster, dato(ff.getTerminDato())), NORMAL));
        if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
            document.add(paragraph(
                    getMessage("termindatotekst", kvitteringstekster, dato(ff.getUtstedtDato())), NORMAL));
        }
    }

    private void blankLine(Document document) throws DocumentException {
        document.add(blankLine());
    }

    private void tileggsOpplysninger(Søknad søknad, Document document) throws DocumentException {
        document.add(paragraph(getMessage("tillegg", kvitteringstekster), HEADING));
        document.add(
                paragraph(Optional.ofNullable(søknad.getTilleggsopplysninger()).orElse("Ingen"), NORMAL));
    }

    private Image logo() throws BadElementException, MalformedURLException, IOException {
        return Image.getInstance(
                StreamUtils.copyToByteArray(new ClassPathResource("pdf/nav-logo.png").getInputStream()));
    }

    private static String navn(Navn søker) {
        return (Optional.ofNullable(søker.getFornavn()).orElse("") + " "
                + Optional.ofNullable(søker.getMellomnavn()).orElse("") + " "
                + Optional.ofNullable(søker.getEtternavn()).orElse("")).trim();
    }

    private static boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof FremtidigFødsel;
    }

    private static boolean erFødt(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof Fødsel;
    }

    String dato(List<LocalDate> fødselsdato) {
        return fødselsdato.stream()
                .distinct()
                .map(s -> dato(s))
                .collect(Collectors.joining(", "));
    }

    private static String dato(LocalDate dato) {
        return dato.format(DATE_FMT);
    }

    private static String countryName(Boolean b) {
        return b ? "Norge" : "utlandet";
    }

    private static Paragraph paragraph(String txt, Font font) {
        return new Paragraph(new Chunk(txt, font));
    }

    private static Paragraph centeredParagraph(String txt, Font font) {
        Paragraph p = paragraph(txt, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private static Element separator() {
        Paragraph p = new Paragraph();
        DottedLineSeparator dottedline = new DottedLineSeparator();
        dottedline.setGap(2f);
        dottedline.setOffset(2);
        p.add(dottedline);
        p.add(blankLine());
        return p;
    }

    private static Element blankLine() {
        Paragraph p = new Paragraph();
        p.add(Chunk.NEWLINE);
        return p;
    }

    private List<String> utenlandsOpphold(List<Utenlandsopphold> opphold) {
        if (opphold.isEmpty()) {
            return Collections.singletonList(getMessage(CountryCode.NO.getAlpha2(), landkoder));
        }
        return opphold.stream()
                .map(this::formatOpphold)
                .collect(Collectors.toList());
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
