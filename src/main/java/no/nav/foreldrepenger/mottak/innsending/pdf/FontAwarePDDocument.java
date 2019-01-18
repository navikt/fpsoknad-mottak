package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FontAwarePDDocument extends PDDocument {
    private static final Logger LOG = LoggerFactory.getLogger(FontAwarePDDocument.class);

    private static final Resource regularFontResource = streamFra("/pdf/NotoSans-Regular.ttf");
    private static final Resource boldFontResource = streamFra("/pdf/NotoSans-Bold.ttf");
    private static final Resource iccProfile = streamFra("/pdf/sRGB.icc");

    private PDFont regularFont;
    private PDFont boldFont;

    public FontAwarePDDocument() throws IOException {
        regularFont = load(regularFontResource);
        boldFont = load(boldFontResource);
        setPdfMetadata(this);
    }

    private static Resource streamFra(String s) {
        return new ClassPathResource(s);
    }

    public PDFont getRegularFont() {
        return regularFont;
    }

    public PDFont getBoldFont() {
        return boldFont;
    }

    private synchronized PDFont load(Resource res) throws IOException {
        try (InputStream is = res.getInputStream()) {
            return PDType0Font.load(this, is);
        } catch (IOException e) {
            LOG.warn("Kunne ikke lese InputStream under lasting av fonter", e);
            throw e;
        }
    }

    private static void setPdfMetadata(PDDocument doc) throws IOException {
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();

        try {
            DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
            dc.setTitle("Søknad om foreldrepenger eller engangsstønad");
            dc.addCreator("NAV");

            PDFAIdentificationSchema id = xmp.createAndAddPFAIdentificationSchema();
            id.setPart(1);
            id.setConformance("B");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new XmpSerializer().serialize(xmp, baos, true);

            PDMetadata metadata = new PDMetadata(doc);
            metadata.importXMPMetadata(baos.toByteArray());
            doc.getDocumentCatalog().setMetadata(metadata);

            InputStream colorProfile = iccProfile.getInputStream();
            PDOutputIntent intent = new PDOutputIntent(doc, colorProfile);
            intent.setInfo("sRGB IEC61966-2.1");
            intent.setOutputCondition("sRGB IEC61966-2.1");
            intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
            intent.setRegistryName("http://www.color.org");
            doc.getDocumentCatalog().addOutputIntent(intent);
        } catch (Exception e) {
            LOG.warn("Setting PDF metadata failed, ignoring error. ", e);
        }
    }
}
