package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.http.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_XML;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class FPFordelKonvoluttGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelKonvoluttGenerator.class);

    static final String HOVEDDOKUMENT = "hoveddokument";
    static final String VEDLEGG = "vedlegg";
    static final String METADATA = "metadata";
    private static final String CONTENT_ID = "Content-ID";
    private final FPFordelMetdataGenerator metadataGenerator;
    private final VersjonsBevisstDomainMapper søknadGenerator;
    private final ForeldrepengerPDFGenerator pdfGenerator;

    public FPFordelKonvoluttGenerator(FPFordelMetdataGenerator metadataGenerator,
            VersjonsBevisstDomainMapper søknadGenerator, ForeldrepengerPDFGenerator pdfGenerator) {
        this.metadataGenerator = metadataGenerator;
        this.søknadGenerator = søknadGenerator;
        this.pdfGenerator = pdfGenerator;
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload(Søknad søknad, Person søker, Versjon versjon) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        LOG.trace("Genererer payload");
        builder.part(METADATA, metadata(søknad, søker.aktørId, MDC.get(NAV_CALL_ID)), APPLICATION_JSON_UTF8);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(søknad, søker.aktørId, versjon), APPLICATION_XML).header(
                CONTENT_ID,
                id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(søknad, søker), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        søknad.getVedlegg().stream()
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));

        return new HttpEntity<>(builder.build(), headers());
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload(Endringssøknad endringsøknad, Person søker,
            Versjon versjon) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);

        builder.part(METADATA, metadata(endringsøknad, søker.aktørId, MDC.get(NAV_CALL_ID)), APPLICATION_JSON_UTF8);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(endringsøknad, søker.aktørId, versjon), APPLICATION_XML).header(
                CONTENT_ID,
                id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(endringsøknad, søker), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        endringsøknad.getVedlegg().stream()
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));

        return new HttpEntity<>(builder.build(), headers());
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload(Ettersending ettersending, Person søker) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        builder.part(METADATA, metadata(ettersending, søker.aktørId, MDC.get(NAV_CALL_ID)), APPLICATION_JSON_UTF8);
        ettersending.getVedlegg().stream()
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));

        return new HttpEntity<>(builder.build(), headers());
    }

    private static void addVedlegg(MultipartBodyBuilder builder, Vedlegg vedlegg, AtomicInteger contentId) {
        LOG.info("Legger til vedlegg av type {} og størrelse {}", vedlegg.getDokumentType(),
                vedlegg.getStørrelse());
        builder.part(VEDLEGG, vedlegg.getVedlegg(), APPLICATION_PDF)
                .headers(headers(vedlegg, contentId));
    }

    private static VedleggHeaderConsumer headers(Vedlegg vedlegg, AtomicInteger contentId) {
        return new VedleggHeaderConsumer(vedlegg.getBeskrivelse(), id(contentId));
    }

    private static String id(AtomicInteger id) {
        return String.valueOf(id.getAndIncrement());
    }

    private static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_MIXED);
        return headers;
    }

    private String metadata(Endringssøknad endringssøknad, AktorId aktørId, String ref) {
        String metadata = metadataGenerator
                .generateMetadata(new FPFordelMetadata(endringssøknad, aktørId, ref));
        LOG.debug("Metadata for endringssøknad er {}", metadata);
        return metadata;

    }

    private String metadata(Søknad søknad, AktorId aktørId, String ref) {
        String metadata = metadataGenerator.generateMetadata(new FPFordelMetadata(søknad, aktørId, ref));
        LOG.debug("Metadata for førstegangssøknad er {}", metadata);
        return metadata;
    }

    private String metadata(Ettersending ettersending, AktorId aktørId, String ref) {
        String metadata = metadataGenerator.generateMetadata(new FPFordelMetadata(ettersending, aktørId, ref));
        LOG.debug("Metadata for ettersending er {}", metadata);
        return metadata;
    }

    private byte[] pdfHovedDokument(Søknad søknad, Person søker) {
        return pdfGenerator.generate(søknad, søker);
    }

    private byte[] pdfHovedDokument(Endringssøknad søknad, Person søker) {
        return pdfGenerator.generate(søknad, søker);
    }

    private String xmlHovedDokument(Søknad søknad, AktorId søker, Versjon versjon) {
        String hovedDokument = søknadGenerator.tilXML(søknad, søker, versjon);
        LOG.debug(CONFIDENTIAL, "Hoveddokument er {}", hovedDokument);
        return hovedDokument;
    }

    private String xmlHovedDokument(Endringssøknad endringssøknad, AktorId søker, Versjon versjon) {
        String hovedDokument = søknadGenerator.tilXML(endringssøknad, søker, versjon);
        LOG.debug(CONFIDENTIAL, "Hoveddokument endringssøknad er {}", hovedDokument);
        return hovedDokument;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [metadataGenerator=" + metadataGenerator + ", søknadGenerator="
                + søknadGenerator + ", pdfGenerator=" + pdfGenerator + "]";
    }

    private static final class VedleggHeaderConsumer implements Consumer<HttpHeaders> {
        private final String filNavn;
        private final String contentId;

        private VedleggHeaderConsumer(String filNavn, String contentId) {
            this.filNavn = filNavn;
            this.contentId = contentId;
        }

        @Override
        public void accept(HttpHeaders headers) {
            headers.setContentDispositionFormData(VEDLEGG, filNavn);
            headers.set(CONTENT_ID, contentId);
        }
    }
}
