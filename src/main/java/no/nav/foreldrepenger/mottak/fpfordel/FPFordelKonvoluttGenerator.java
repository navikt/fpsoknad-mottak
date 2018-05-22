package no.nav.foreldrepenger.mottak.fpfordel;

import static org.apache.http.Consts.UTF_8;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;
import static org.apache.http.entity.mime.HttpMultipartMode.RFC6532;
import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@Component
public class FPFordelKonvoluttGenerator {

    private static final ContentType APPLICATION_PDF = ContentType.create("application/pdf");

    private static final String HOVEDDOKUMENT = "hoveddokument";
    private final FPFordelMetdataGenerator metadataGenerator;
    private final FPFordelSøknadGenerator søknadGenerator;
    private final ForeldrepengerPDFGenerator pdfGenerator;

    public FPFordelKonvoluttGenerator(FPFordelMetdataGenerator metadataGenerator,
            FPFordelSøknadGenerator søknadGenerator, ForeldrepengerPDFGenerator pdfGenerator) {
        this.metadataGenerator = metadataGenerator;
        this.søknadGenerator = søknadGenerator;
        this.pdfGenerator = pdfGenerator;
    }

    public byte[] createPayload(Søknad søknad, AktorId aktørId, String ref) {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMimeSubtype("mixed")
                .setMode(RFC6532);

        final AtomicInteger id = new AtomicInteger(1);

        builder.addPart(buildPart("metadata", metadata(søknad, aktørId, ref)))
                .addPart(buildPart(HOVEDDOKUMENT, xmlDocument(søknad, aktørId), id))
                .addPart(buildPart(HOVEDDOKUMENT, pdfDocument(søknad), id, true));

        return medVedlegg(søknad, builder, id);

    }

    private static byte[] medVedlegg(Søknad søknad, MultipartEntityBuilder builder,
            final AtomicInteger id) {
        søknad.getVedlegg()
                .stream()
                .forEach(s -> addVedlegg(s.getVedlegg(), builder, id));
        return toByteArray(builder.build());

    }

    private static byte[] toByteArray(HttpEntity entity) {
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private ContentBody pdfDocument(Søknad søknad) {
        return new ByteArrayBody(encode(pdfGenerator.generate(søknad)),
                APPLICATION_PDF,
                HOVEDDOKUMENT + ".pdf");
    }

    private ContentBody xmlDocument(Søknad søknad, AktorId aktørId) {
        return new ByteArrayBody(søknadGenerator.toXML(søknad, aktørId).getBytes(),
                APPLICATION_XML.withCharset(UTF_8),
                HOVEDDOKUMENT + ".xml");
    }

    private ContentBody metadata(Søknad søknad, AktorId aktorId, String ref) {
        return new ByteArrayBody(
                metadataGenerator.generateMetadata(new FPFordelMetadata(søknad, aktorId, ref)).getBytes(),
                APPLICATION_JSON, "metadata.json");
    }

    private static byte[] encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    private static MultipartEntityBuilder addVedlegg(byte[] vedlegg, MultipartEntityBuilder builder, AtomicInteger id) {
        String vedleggNavn = "vedlegg-" + String.valueOf(id);
        return builder.addPart(buildPart(vedleggNavn, new ByteArrayBody(encode(vedlegg), APPLICATION_PDF,
                vedleggNavn + ".pdf"), id, true));
    }

    private static FormBodyPart buildPart(String name, ContentBody body) {
        return buildPart(name, body, null);
    }

    private static FormBodyPart buildPart(String name, ContentBody body, AtomicInteger contentId) {
        return buildPart(name, body, contentId, false);
    }

    private static FormBodyPart buildPart(String name, ContentBody body, AtomicInteger contentId, boolean isBase64) {
        FormBodyPartBuilder builder = FormBodyPartBuilder.create()
                .setName(name)
                .setBody(body);
        if (isBase64) {
            builder.setField(CONTENT_ENCODING, "base64");
        }
        if (contentId != null) {
            builder.setField("Content-ID", String.valueOf(contentId.getAndIncrement()));
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [metadataGenerator=" + metadataGenerator + ", søknadGenerator="
                + søknadGenerator + ", pdfGenerator=" + pdfGenerator + "]";
    }

}
