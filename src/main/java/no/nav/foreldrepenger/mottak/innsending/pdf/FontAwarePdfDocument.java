package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.SØKNAD_OUTLINE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.xml.XmpSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import no.nav.foreldrepenger.mottak.error.UnexpectedInputException;

public class FontAwarePdfDocument extends PDDocument {
    private static final String S_RGB_IEC61966_2_1 = "sRGB IEC61966-2.1";

    private static final Logger LOG = LoggerFactory.getLogger(FontAwarePdfDocument.class);

    private static final Resource REGULAR = new ClassPathResource("/pdf/NotoSans-Regular.ttf");
    private static final Resource BOLD = new ClassPathResource("/pdf/NotoSans-Bold.ttf");
    private static final Resource ICC = new ClassPathResource("/pdf/sRGB.icc");

    private final PDFont regularFont;
    private final PDFont boldFont;

    private final PDDocumentOutline outline = new PDDocumentOutline();

    private PDOutlineItem pagesOutline;

    FontAwarePdfDocument() {
        regularFont = load(REGULAR);
        boldFont = load(BOLD);
        setPdfMetadata(this);
        this.getDocumentCatalog().setDocumentOutline(outline);
        setPagesOutline();
    }

    PDFont getRegularFont() {
        return regularFont;
    }

    PDFont getBoldFont() {
        return boldFont;
    }

    PDOutlineItem getPagesOutline() {
        return pagesOutline;
    }

    private synchronized PDFont load(Resource res) {
        if (res.exists()) {
            try (var is = res.getInputStream()) {
                return PDType0Font.load(this, is);
            } catch (IOException e) {
                throw new UnexpectedInputException("Kunne ikke lese InputStream under lasting av fonter", e);
            }
        }
        throw new UnexpectedInputException("Font " + res.getFilename() + " finnes ikke");
    }

    private static void setPdfMetadata(PDDocument doc) {
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();

        try {
            var dc = xmp.createAndAddDublinCoreSchema();
            dc.setTitle("Søknad");
            dc.addCreator("NAV");

            var id = xmp.createAndAddPFAIdentificationSchema();
            id.setPart(1);
            id.setConformance("B");

            var baos = new ByteArrayOutputStream();
            new XmpSerializer().serialize(xmp, baos, true);

            var metadata = new PDMetadata(doc);
            metadata.importXMPMetadata(baos.toByteArray());
            doc.getDocumentCatalog().setMetadata(metadata);

            var colorProfile = ICC.getInputStream();
            var intent = new PDOutputIntent(doc, colorProfile);
            intent.setInfo(S_RGB_IEC61966_2_1);
            intent.setOutputCondition(S_RGB_IEC61966_2_1);
            intent.setOutputConditionIdentifier(S_RGB_IEC61966_2_1);
            intent.setRegistryName("http://www.color.org");
            doc.getDocumentCatalog().addOutputIntent(intent);
        } catch (Exception e) {
            LOG.warn("Setting PDF metadata failed, ignoring error. ", e);
        }
    }

    private void setPagesOutline() {
        pagesOutline = new PDOutlineItem();
        pagesOutline.setTitle(SØKNAD_OUTLINE.getTitle());
        outline.addLast(pagesOutline);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [regularFont=" + regularFont + ", boldFont=" + boldFont + "]";
    }
}
