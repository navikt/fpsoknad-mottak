package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter.MULTIPART_MIXED;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;
import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_XML;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
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
        FPFordelMetadata metadata = metadataFor(søknad, egenskap.getType(), søker.getAktørId(), callId());
        builder.part(METADATA, metadata(metadata), APPLICATION_JSON_UTF8);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(søknad, søker.getAktørId(), egenskap), APPLICATION_XML)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(søknad, søker, egenskap), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        safeStream(søknad.getVedlegg())
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));
        return new FPFordelKonvolutt(egenskap, søknad, new HttpEntity<>(builder.build(), headers()),
                vedleggFra(metadata, false));
    }

    public FPFordelKonvolutt generer(Endringssøknad endringsøknad, Person søker, SøknadEgenskap egenskap) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        FPFordelMetadata metadata = metadataFor(endringsøknad, egenskap, søker.getAktørId(), callId());
        builder.part(METADATA, metadata(metadata), APPLICATION_JSON_UTF8);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(endringsøknad, søker.getAktørId(), egenskap), APPLICATION_XML)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(endringsøknad, søker, egenskap), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        safeStream(endringsøknad.getVedlegg())
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));
        return new FPFordelKonvolutt(egenskap, endringsøknad, new HttpEntity<>(builder.build(), headers()),
                vedleggFra(metadata, false));
    }

    public FPFordelKonvolutt generer(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        FPFordelMetadata metadata = metadataFor(ettersending, søker.getAktørId(), callId());
        builder.part(METADATA, metadata(metadata), APPLICATION_JSON_UTF8);
        safeStream(ettersending.getVedlegg())
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));
        return new FPFordelKonvolutt(egenskap, ettersending, new HttpEntity<>(builder.build(), headers()),
                vedleggFra(metadata));
    }

    private static void addVedlegg(MultipartBodyBuilder builder, Vedlegg vedlegg, AtomicInteger contentId) {
        if (vedlegg.getStørrelse() == 0) {
            LOG.warn("Vedlegg {} har størrelse 0, kan ikke sendes", vedlegg);
        } else {
            LOG.info("Legger til vedlegg av type {} og størrelse {}", vedlegg.getDokumentType(),
                    vedlegg.getStørrelse());
            builder.part(VEDLEGG, vedlegg.getVedlegg(), APPLICATION_PDF)
                    .headers(headers(vedlegg, contentId));
        }
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

    private String metadata(FPFordelMetadata m) {
        String metadata = metadataGenerator.generer(m);
        LOG.debug("Metadata er {}", metadata);
        return metadata;
    }

    private static FPFordelMetadata metadataFor(Endringssøknad endringssøknad, SøknadEgenskap egenskap, AktørId aktørId,
            String ref) {
        return new FPFordelMetadata(endringssøknad, egenskap.getType(), aktørId, ref);
    }

    private static FPFordelMetadata metadataFor(Søknad søknad, SøknadType type, AktørId aktørId, String ref) {
        return new FPFordelMetadata(søknad, type, aktørId, ref);
    }

    private static FPFordelMetadata metadataFor(Ettersending ettersending, AktørId aktørId, String ref) {
        return new FPFordelMetadata(ettersending, aktørId, ref);
    }

    private byte[] pdfHovedDokument(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return pdfGenerator.generate(søknad, søker, egenskap);
    }

    private byte[] pdfHovedDokument(Endringssøknad endringssøknad, Person søker, SøknadEgenskap egenskap) {
        return pdfGenerator.generate(endringssøknad, søker, egenskap);
    }

    private String xmlHovedDokument(Søknad søknad, AktørId søker, SøknadEgenskap egenskap) {
        String hovedDokument = domainMapper.tilXML(søknad, søker, egenskap);
        LOG.debug(CONFIDENTIAL, "Hoveddokument er {}", hovedDokument);
        return hovedDokument;
    }

    private String xmlHovedDokument(Endringssøknad endringssøknad, AktørId søker, SøknadEgenskap egenskap) {
        String hovedDokument = domainMapper.tilXML(endringssøknad, søker, egenskap);
        LOG.debug(CONFIDENTIAL, "Hoveddokument endringssøknad er {}", hovedDokument);
        return hovedDokument;
    }

    private static List<String> vedleggFra(FPFordelMetadata metadata) {
        return vedleggFra(metadata, true);
    }

    private static List<String> vedleggFra(FPFordelMetadata metadata, boolean erEttersending) {
        if (erEttersending) {
            return metadata.getFiler()
                    .stream()
                    .map(Del::getDokumentTypeId)
                    .collect(Collectors.toList());
        }
        return metadata.getFiler()
                .stream()
                .skip(2)
                .map(Del::getDokumentTypeId)
                .collect(Collectors.toList());
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
