package no.nav.foreldrepenger.mottak.fpfordel;

import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@Component
public class FPFordelSpringKonvoluttGenerator {

    private static final String CONTENT_ID = "Content-ID";
    private static final String HOVEDDOKUMENT = "hoveddokument";
    private final FPFordelMetdataGenerator metadataGenerator;
    private final FPFordelSøknadGenerator søknadGenerator;
    private final ForeldrepengerPDFGenerator pdfGenerator;

    public FPFordelSpringKonvoluttGenerator(FPFordelMetdataGenerator metadataGenerator,
            FPFordelSøknadGenerator søknadGenerator, ForeldrepengerPDFGenerator pdfGenerator) {
        this.metadataGenerator = metadataGenerator;
        this.søknadGenerator = søknadGenerator;
        this.pdfGenerator = pdfGenerator;
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload(Søknad søknad, AktorId aktørId, String ref) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        builder.part("metadata", metadata(søknad, aktørId, ref), APPLICATION_JSON)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(søknad, aktørId), APPLICATION_XML)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(søknad), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");

        søknad.getVedlegg()
                .stream()
                .map(vedlegg -> vedlegg.getVedlegg())
                .map(this::encode)
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));

        return new HttpEntity<>(builder.build(), headers());
    }

    private static void addVedlegg(MultipartBodyBuilder builder, byte[] vedlegg, AtomicInteger id) {
        builder.part("vedlegg", vedlegg, APPLICATION_PDF)
                .header(CONTENT_ID, id(id));
    }

    private static String id(AtomicInteger id) {
        return String.valueOf(id.getAndIncrement());
    }

    private static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);
        return headers;
    }

    private String metadata(Søknad søknad, AktorId aktørId, String ref) {
        return metadataGenerator.generateMetadata(new FPFordelMetadata(søknad, aktørId, ref));
    }

    private byte[] pdfHovedDokument(Søknad søknad) {
        return encode(pdfGenerator.generate(søknad));
    }

    private String xmlHovedDokument(Søknad søknad, AktorId aktørId) {
        return søknadGenerator.toXML(søknad, aktørId);
    }

    private byte[] encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [metadataGenerator=" + metadataGenerator + ", søknadGenerator="
                + søknadGenerator + ", pdfGenerator=" + pdfGenerator + "]";
    }
}
