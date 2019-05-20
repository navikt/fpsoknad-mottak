package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class InfoskrivPdfExtracter {
    public static final Logger LOG = LoggerFactory.getLogger(InfoskrivPdfExtracter.class);
    private final static String INFOSKRIV_OUTLINETITLE = "Informasjon til arbeidsgiver(e)";

    public byte[] extractInfoskriv(byte[] pdf) {
        try (PDDocument doc = PDDocument.load(pdf)) {
            PDDocumentOutline outline = doc.getDocumentCatalog().getDocumentOutline();
            PDOutlineNode node = outline.getFirstChild();
            int splitFrom = infoskrivStartpage(node);
            if (splitFrom > -1) {
                return extractPagesFromPdf(doc, splitFrom);
            }
        } catch (IOException e) {
            LOG.warn("Feil ved ekstrahering av infoskriv fra søknadspdf, dropper infoskriv", e);
        }
        return null;
    }

    private static byte[] extractPagesFromPdf(PDDocument doc, int page) throws IOException {
        PageExtractor pe = new PageExtractor(doc);
        pe.setStartPage(page);
        PDDocument infodoc = pe.extract();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        infodoc.save(baos);
        return baos.toByteArray();
    }

    private static int infoskrivStartpage(PDOutlineNode bm) {
        try {
            for (PDOutlineItem node : bm.children()) {
                if (node.getTitle().equals(INFOSKRIV_OUTLINETITLE)) {
                    PDPageDestination destination = (PDPageDestination) node.getDestination();
                    return destination.retrievePageNumber() + 1;
                }
            }
        } catch (IOException swallow) {
            LOG.warn("Ingen PDPageDestination på noden, defaulter til ingen treff på søk etter infoskriv");
        }
        return -1;
    }
}
