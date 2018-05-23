package no.nav.foreldrepenger.mottak.pdf;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
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
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Navn;

public abstract class AbstractPDFGenerator {

    private final Font HEADING = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
    private final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.uuuu");

    @Inject
    @Qualifier("landkoder")
    private MessageSource landkoder;

    @Inject
    @Qualifier("kvitteringstekster")
    private MessageSource kvitteringstekster;

    protected void logo(Document document)
            throws IOException, DocumentException {
        Image logo = logo();
        logo.setAlignment(Image.ALIGN_CENTER);
        document.add(logo);
    }

    protected Paragraph heading(String txt) {
        return paragraph(txt, HEADING);
    }

    protected Paragraph regularParagraph(String txt) {
        return paragraph(txt, NORMAL);
    }

    protected Paragraph center(Paragraph paragraph) {
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }

    protected Element blankLine() {
        Paragraph p = new Paragraph();
        p.add(Chunk.NEWLINE);
        return p;
    }

    protected Element separator() {
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

    protected com.itextpdf.text.List bulletedList(java.util.List<String> elements) {
        com.itextpdf.text.List bulletedList = new com.itextpdf.text.List();
        bulletedList.setListSymbol(new Chunk("\u2022", new Font()));
        bulletedList.setSymbolIndent(12);
        elements.stream().forEach(s -> bulletedList.add(s));
        return bulletedList;
    }

    protected String navnToString(Navn navn) {
        return (Optional.ofNullable(navn.getFornavn()).orElse("") + " "
                + Optional.ofNullable(navn.getMellomnavn()).orElse("") + " "
                + Optional.ofNullable(navn.getEtternavn()).orElse("")).trim();
    }

    protected String dato(LocalDate localDate) {
        return localDate.format(DATE_FMT);
    }

    protected String dato(List<LocalDate> dates) {
        return dates.stream().map(d -> dato(d)).collect(joining(", "));
    }

    private Image logo() throws BadElementException, IOException {
        return Image.getInstance(
                StreamUtils.copyToByteArray(new ClassPathResource("pdf/nav-logo.png").getInputStream()));
    }

    private Paragraph paragraph(String txt, Font font) {
        return new Paragraph(new Chunk(txt, font));
    }

    private String getMessage(String key, MessageSource messages, Object... values) {
        return getMessage(key, null, messages, values);
    }

    private String getMessage(String key, String defaultValue, MessageSource messages, Object... values) {
        ((ResourceBundleMessageSource) messages).setDefaultEncoding("utf-8");
        return messages.getMessage(key, values, defaultValue, CountryCode.NO.toLocale());
    }

}
