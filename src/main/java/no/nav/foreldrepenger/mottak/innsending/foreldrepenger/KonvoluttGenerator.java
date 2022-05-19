package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.util.MDCUtil.callId;
import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.MULTIPART_MIXED;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.felles.DokumentType;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsending.pdf.MappablePdfGenerator;

@Component
public class KonvoluttGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(KonvoluttGenerator.class);
    private static final String CONTENT_ID = "Content-ID";

    static final String HOVEDDOKUMENT = "hoveddokument";
    static final String VEDLEGG = "vedlegg";
    static final String METADATA = "metadata";
    private final MetdataGenerator metadataGenerator;
    private final DomainMapper domainMapper;
    private final MappablePdfGenerator pdfGenerator;

    public KonvoluttGenerator(MetdataGenerator metadataGenerator,
            @Qualifier(DELEGERENDE) DomainMapper domainMapper,
            @Qualifier(DELEGERENDE) MappablePdfGenerator pdfGenerator) {
        this.metadataGenerator = metadataGenerator;
        this.domainMapper = domainMapper;
        this.pdfGenerator = pdfGenerator;
    }

    public Konvolutt generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        LOG.trace("Genererer konvolutt fra søknad {}", søknad);
        var id = new AtomicInteger(1);
        var builder = new MultipartBodyBuilder();
        builder.part(METADATA, metadataFor(søknad, egenskap.getType(), søker.aktørId()), APPLICATION_JSON);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(søknad, søker.aktørId(), egenskap), APPLICATION_XML)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(søknad, søker, egenskap), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        safeStream(søknad.getVedlegg())
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));
        return new Konvolutt(egenskap, søknad, new HttpEntity<>(builder.build(), headers()),
                opplastedeVedleggFra(søknad), ikkeOpplastedeVedleggFra(søknad));
    }

    public Konvolutt generer(Endringssøknad endringsøknad, Person søker, SøknadEgenskap egenskap) {
        var id = new AtomicInteger(1);
        var builder = new MultipartBodyBuilder();
        builder.part(METADATA, metadataFor(endringsøknad, egenskap.getType(), søker.aktørId()),
                APPLICATION_JSON);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(endringsøknad, søker.aktørId(), egenskap), APPLICATION_XML)
                .header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(endringsøknad, søker, egenskap), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        safeStream(endringsøknad.getVedlegg())
                .filter(s -> LASTET_OPP.equals(s.getInnsendingsType()))
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));
        return new Konvolutt(egenskap, endringsøknad, new HttpEntity<>(builder.build(), headers()),
                opplastedeVedleggFra(endringsøknad), ikkeOpplastedeVedleggFra(endringsøknad));
    }

    public Konvolutt generer(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        var id = new AtomicInteger(1);
        var builder = new MultipartBodyBuilder();
        builder.part(METADATA, metadataFor(ettersending, søker.aktørId()), APPLICATION_JSON);
        safeStream(ettersending.vedlegg())
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));
        return new Konvolutt(egenskap, ettersending, new HttpEntity<>(builder.build(), headers()),
                opplastedeVedleggFra(ettersending), ikkeOpplastedeVedleggFra(ettersending));
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
        var headers = new HttpHeaders();
        headers.setContentType(MULTIPART_MIXED);
        return headers;
    }

    private String metadataFor(Endringssøknad endringssøknad, SøknadType type, AktørId aktørId) {
        return metadataGenerator.generer(new FordelMetadata(endringssøknad, type, aktørId, callId()));
    }

    private String metadataFor(Søknad søknad, SøknadType type, AktørId aktørId) {
        return metadataGenerator.generer(new FordelMetadata(søknad, type, aktørId, callId()));
    }

    private String metadataFor(Ettersending ettersending, AktørId aktørId) {
        return metadataGenerator.generer(new FordelMetadata(ettersending, aktørId, callId()));
    }

    private byte[] pdfHovedDokument(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return pdfGenerator.generer(søknad, søker, egenskap);
    }

    private byte[] pdfHovedDokument(Endringssøknad endringssøknad, Person søker, SøknadEgenskap egenskap) {
        return pdfGenerator.generer(endringssøknad, søker, egenskap);
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

    private static List<String> opplastedeVedleggFra(Søknad søknad) {
        return vedleggMedTypeFra(søknad.getVedlegg(), LASTET_OPP);
    }

    private static List<String> ikkeOpplastedeVedleggFra(Søknad søknad) {
        return vedleggMedTypeFra(søknad.getVedlegg(), SEND_SENERE);
    }

    private static List<String> opplastedeVedleggFra(Ettersending es) {
        return vedleggMedTypeFra(es.vedlegg(), LASTET_OPP);
    }

    private static List<String> ikkeOpplastedeVedleggFra(Ettersending es) {
        return vedleggMedTypeFra(es.vedlegg(), SEND_SENERE);
    }

    private static List<String> vedleggMedTypeFra(List<Vedlegg> vedlegg, InnsendingsType type) {
        return safeStream(vedlegg)
                .filter(v -> type.equals(v.getInnsendingsType()))
                .map(Vedlegg::getDokumentType)
                .map(DokumentType::name)
                .toList();
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
