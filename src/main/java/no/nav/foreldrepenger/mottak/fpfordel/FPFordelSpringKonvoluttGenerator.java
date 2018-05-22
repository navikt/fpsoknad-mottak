package no.nav.foreldrepenger.mottak.fpfordel;

import java.net.URI;

import org.apache.http.entity.ContentType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

public class FPFordelSpringKonvoluttGenerator {
    private static final String HOVEDDOKUMENT = "hoveddokument";
    private static final ContentType APPLICATION_PDF = ContentType.create("application/pdf");
    private final FPFordelMetdataGenerator metadataGenerator;
    private final FPFordelSøknadGenerator søknadGenerator;
    private final ForeldrepengerPDFGenerator pdfGenerator;

    public FPFordelSpringKonvoluttGenerator(FPFordelMetdataGenerator metadataGenerator,
            FPFordelSøknadGenerator søknadGenerator, ForeldrepengerPDFGenerator pdfGenerator) {
        this.metadataGenerator = metadataGenerator;
        this.søknadGenerator = søknadGenerator;
        this.pdfGenerator = pdfGenerator;
    }

    public void createPayload(Søknad søknad, String ref) {
        MultiValueMap<String, Object> multiPartBody = new LinkedMultiValueMap<>();
        multiPartBody.add("file", new ClassPathResource("/uploadFiles/User.txt"));

        RequestEntity<MultiValueMap<String, Object>> requestEntity = RequestEntity
                .post(URI.create(""))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(multiPartBody);
    }

}
