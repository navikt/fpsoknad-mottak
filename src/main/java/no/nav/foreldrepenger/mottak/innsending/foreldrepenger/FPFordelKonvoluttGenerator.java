package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_XML;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.PDFGenerator;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Component
public class FPFordelKonvoluttGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelKonvoluttGenerator.class);

    static final String HOVEDDOKUMENT = "hoveddokument";
    static final String VEDLEGG = "vedlegg";
    static final String METADATA = "metadata";
    private static final String CONTENT_ID = "Content-ID";
    private final FPFordelMetdataGenerator metadataGenerator;
    private final DomainMapper domainMapper;
    private final PDFGenerator pdfGenerator;

    public FPFordelKonvoluttGenerator(FPFordelMetdataGenerator metadataGenerator,
            @Qualifier(DELEGERENDE) DomainMapper domainMapper,
            @Qualifier(DELEGERENDE) PDFGenerator pdfGenerator) {
        this.metadataGenerator = metadataGenerator;
        this.domainMapper = domainMapper;
        this.pdfGenerator = pdfGenerator;
    }

    public FPFordelKonvolutt generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        LOG.trace("Genererer payload med oversendelsesid {}", callId());
        builder.part(METADATA, metadata(søknad, egenskap, søker.aktørId, callId()), APPLICATION_JSON_UTF8);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(søknad, søker.aktørId, egenskap), APPLICATION_XML)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(søknad, søker, egenskap), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        søknad.getVedlegg().stream()
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));

        return new FPFordelKonvolutt(new HttpEntity<>(builder.build(), headers()));
    }

    public FPFordelKonvolutt generer(Endringssøknad endringsøknad, Person søker, SøknadEgenskap egenskap) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);

        builder.part(METADATA, metadata(endringsøknad, egenskap, søker.aktørId, callId()), APPLICATION_JSON_UTF8);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(endringsøknad, søker.aktørId, egenskap), APPLICATION_XML)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(endringsøknad, søker, egenskap), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        endringsøknad.getVedlegg().stream()
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));
        return new FPFordelKonvolutt(new HttpEntity<>(builder.build(), headers()));
    }

    public FPFordelKonvolutt generer(Ettersending ettersending, Person søker) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        builder.part(METADATA, metadata(ettersending, søker.aktørId, callId()), APPLICATION_JSON_UTF8);
        ettersending.getVedlegg().stream()
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));

        return new FPFordelKonvolutt(new HttpEntity<>(builder.build(), headers()));
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

    private String metadata(Endringssøknad endringssøknad, SøknadEgenskap egenskap, AktorId aktørId, String ref) {
        String metadata = metadataGenerator
                .generer(new FPFordelMetadata(endringssøknad, egenskap.getType(), aktørId, ref));
        LOG.debug("Metadata for endringssøknad er {}", metadata);
        return metadata;

    }

    private String metadata(Søknad søknad, SøknadEgenskap egenskap, AktorId aktørId, String ref) {
        String metadata = metadataGenerator
                .generer(new FPFordelMetadata(søknad, egenskap.getType(), aktørId, ref));
        LOG.debug("Metadata for førstegangssøknad er {}", metadata);
        return metadata;
    }

    private String metadata(Ettersending ettersending, AktorId aktørId, String ref) {
        String metadata = metadataGenerator.generer(new FPFordelMetadata(ettersending, aktørId, ref));
        LOG.debug("Metadata for ettersending er {}", metadata);
        return metadata;
    }

    private byte[] pdfHovedDokument(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return pdfGenerator.generate(søknad, søker, egenskap);
    }

    private byte[] pdfHovedDokument(Endringssøknad endringssøknad, Person søker, SøknadEgenskap egenskap) {
        return pdfGenerator.generate(endringssøknad, søker, egenskap);
    }

    private String xmlHovedDokument(Søknad søknad, AktorId søker, SøknadEgenskap egenskap) {
        String hovedDokument = domainMapper.tilXML(søknad, søker, egenskap);
        LOG.debug(CONFIDENTIAL, "Hoveddokument er {}", hovedDokument);
        return hovedDokument;
    }

    private String xmlHovedDokument(Endringssøknad endringssøknad, AktorId søker, SøknadEgenskap egenskap) {
        String hovedDokument = domainMapper.tilXML(endringssøknad, søker, egenskap);
        LOG.debug(CONFIDENTIAL, "Hoveddokument endringssøknad er {}", hovedDokument);
        return hovedDokument;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [metadataGenerator=" + metadataGenerator + ", domainMapper="
                + domainMapper + ", pdfGenerator=" + pdfGenerator + "]";
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
