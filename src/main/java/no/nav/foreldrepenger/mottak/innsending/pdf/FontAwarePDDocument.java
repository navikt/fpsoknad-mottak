package no.nav.foreldrepenger.mottak.innsending.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.XmpSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FontAwarePDDocument extends PDDocument {
    public static PDType0Font REGULARFONT;
    public static PDType0Font BOLDFONT;
    private static final Logger LOG = LoggerFactory.getLogger(FontAwarePDDocument.class);

    public FontAwarePDDocument() throws IOException {
        BOLDFONT = PDType0Font.load(this, new ClassPathResource("/pdf/NotoSans-Bold.ttf").getInputStream());
        REGULARFONT = PDType0Font.load(this, new ClassPathResource("/pdf/NotoSans-Regular.ttf").getInputStream());
        setPdfMetadata(this);
    }

    private void setPdfMetadata(PDDocument doc) throws IOException {
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();

        try {
            DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
            dc.setTitle("Søknad om foreldrepenger eller engangsstønad");

            PDFAIdentificationSchema id = xmp.createAndAddPFAIdentificationSchema();
            id.setPart(1);
            id.setConformance("B");

            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmp, baos, true);

            PDMetadata metadata = new PDMetadata(doc);
            metadata.importXMPMetadata(baos.toByteArray());
            doc.getDocumentCatalog().setMetadata(metadata);

            InputStream colorProfile = FontAwarePDDocument.class.getResourceAsStream(
                "/pdf/sRGB.icc");
            PDOutputIntent intent = new PDOutputIntent(doc, colorProfile);
            intent.setInfo("sRGB IEC61966-2.1");
            intent.setOutputCondition("sRGB IEC61966-2.1");
            intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
            intent.setRegistryName("http://www.color.org");
            doc.getDocumentCatalog().addOutputIntent(intent);
        }
        catch(Exception e) {
           LOG.warn("Setting PDF metadata failed, ignoring error. " + e);
        }
    }
}
