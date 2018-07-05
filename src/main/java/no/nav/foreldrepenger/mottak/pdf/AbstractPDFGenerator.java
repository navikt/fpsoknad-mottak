package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.joining;
import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Regnskapsfører;

public abstract class AbstractPDFGenerator {

    private static final String LOGO = "pdf/nav-logo.png";
    private static final Font HEADING = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    private final MessageSource landkoder;

    private final MessageSource kvitteringstekster;
    private final Locale locale;

    public AbstractPDFGenerator(@Qualifier("landkoder") MessageSource landkoder,
            @Qualifier("kvitteringstekster") MessageSource kvitteringstekster, Locale locale) {
        this.landkoder = landkoder;
        this.kvitteringstekster = kvitteringstekster;
        this.locale = locale;
    }

    protected static void logo(Document document)
            throws IOException, DocumentException {
        Image logo = logo();
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);
    }

    protected static Paragraph heading(String txt) {
        return paragraph(txt, HEADING);
    }

    protected static Paragraph regularParagraph(String txt) {
        return paragraph(txt, NORMAL);
    }

    protected static Paragraph center(Paragraph paragraph) {
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }

    protected static Element blankLine() {
        Paragraph p = new Paragraph();
        p.add(Chunk.NEWLINE);
        return p;
    }

    protected static Element separator() {
        Paragraph p = new Paragraph();
        DottedLineSeparator dottedline = new DottedLineSeparator();
        dottedline.setGap(2f);
        dottedline.setOffset(2);
        p.add(dottedline);
        p.add(blankLine());
        return p;
    }

    protected String countryName(String isoCode, Object... values) {
        return getMessage(isoCode, landkoder, values);
    }

    protected String fromMessageSource(String key, Object... values) {
        return getMessage(key, kvitteringstekster, values);
    }

    protected static com.itextpdf.text.List bulletedList(java.util.List<String> elements) {
        com.itextpdf.text.List bulletedList = new com.itextpdf.text.List();
        bulletedList.setListSymbol(new Chunk("\u2022", new Font()));
        bulletedList.setSymbolIndent(12);
        elements.stream().forEach(s -> bulletedList.add(s));
        return bulletedList;
    }

    protected static String navnToString(List<Regnskapsfører> regnskapsførere) {
        return regnskapsførere.stream()
                .map(s -> s.getNavn())
                .map(AbstractPDFGenerator::navnToString)
                .collect(Collectors.joining(","));
    }

    protected static String navnToString(Navn navn) {
        return (formatNavn(navn.getFornavn()) + " "
                + formatNavn(navn.getMellomnavn()) + " "
                + formatNavn(navn.getEtternavn()) + " ").trim();
    }

    private static String formatNavn(String navn) {
        return Optional.ofNullable(navn).orElse("");
    }

    protected static String dato(LocalDate localDate) {
        return localDate.format(DATE_FMT);
    }

    protected static String dato(List<LocalDate> dates) {
        return dates.stream()
                .map(d -> dato(d))
                .collect(joining(", "));
    }

    private static Image logo() throws BadElementException, IOException {
        return Image.getInstance(copyToByteArray(new ClassPathResource(LOGO).getInputStream()));
    }

    private static Paragraph paragraph(String txt, Font font) {
        return new Paragraph(new Chunk(txt, font));
    }

    private String getMessage(String key, MessageSource messages, Object... values) {
        return getMessage(key, null, messages, values);
    }

    private String getMessage(String key, String defaultValue, MessageSource messages, Object... values) {
        ((ResourceBundleMessageSource) messages).setDefaultEncoding("utf-8");
        return messages.getMessage(key, values, defaultValue, locale);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [landkoder=" + landkoder + ", kvitteringstekster=" + kvitteringstekster
                + "]";
    }
}
