package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.mottak.innsending.pdf.PdfOutlineItem.INFOSKRIV_OUTLINE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InfoskrivPdfExtractor {

    public static final Logger LOG = LoggerFactory.getLogger(InfoskrivPdfExtractor.class);

    public byte[] extractInfoskriv(byte[] pdf) {
        try (PDDocument doc = PDDocument.load(pdf)) {
            PDDocumentOutline outline = doc.getDocumentCatalog().getDocumentOutline();
            PDOutlineNode node = outline.getFirstChild();
            int startpageExtraction = infoskrivStartpage(node);
            if (startpageExtraction > -1) {
                return extractPagesFrom(doc, startpageExtraction);
            }
        } catch (IOException e) {
            LOG.warn("Feil ved ekstrahering fra søknadspdf, dropper infoskriv", e);
        }
        return null;
    }

    private static byte[] extractPagesFrom(PDDocument doc, int page) throws IOException {
        PageExtractor pe = new PageExtractor(doc);
        pe.setStartPage(page);
        PDDocument infodoc = pe.extract();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        infodoc.save(baos);
        infodoc.close();
        return baos.toByteArray();
    }

    private static int infoskrivStartpage(PDOutlineNode bm) {
        try {
            for (PDOutlineItem node : bm.children()) {
                if (node.getTitle().equals(INFOSKRIV_OUTLINE.getTitle())) {
                    PDPageDestination destination = (PDPageDestination) node.getDestination();
                    return destination.retrievePageNumber() + 1;
                }
            }
        } catch (IOException swallow) {
            LOG.warn("Feil ved leting etter PDPageDestination på noden, defaulter til ingen treff");
        }
        return -1;
    }

}
