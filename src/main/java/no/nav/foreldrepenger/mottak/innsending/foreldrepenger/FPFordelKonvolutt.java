package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.METADATA;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.VEDLEGG;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

public class FPFordelKonvolutt {
    private final HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload;
    private final List<String> vedlegg;

    public FPFordelKonvolutt(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload, List<String> vedlegg) {
        this.payload = payload;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> getPayload() {
        return payload;
    }

    public List<String> getVedleggIds() {
        return vedlegg;
    }

    public String getMetadata() {
        return get(METADATA)
                .filter(mediaType(APPLICATION_JSON_UTF8_VALUE))
                .findFirst()
                .filter(HttpEntity::hasBody)
                .map(HttpEntity::getBody)
                .map(String.class::cast)
                .orElse(null);
    }

    public List<byte[]> getVedlegg() {
        return get(VEDLEGG)
                .filter(mediaType(APPLICATION_PDF_VALUE))
                .filter(HttpEntity::hasBody)
                .map(HttpEntity::getBody)
                .map(byte[].class::cast)
                .collect(toList());
    }

    public String XMLHovedDokument() {
        return get(HOVEDDOKUMENT)
                .filter(mediaType(APPLICATION_XML_VALUE))
                .findFirst()
                .filter(HttpEntity::hasBody)
                .map(HttpEntity::getBody)
                .map(String.class::cast)
                .orElse(null);
    }

    public byte[] PDFHovedDokument() {
        return get(HOVEDDOKUMENT)
                .filter(mediaType(APPLICATION_PDF_VALUE))
                .findFirst()
                .filter(HttpEntity::hasBody)
                .map(HttpEntity::getBody)
                .map(byte[].class::cast)
                .orElse(null);
    }

    private Stream<HttpEntity<?>> get(String key) {
        return Optional.ofNullable(payload)
                .filter(HttpEntity::hasBody)
                .map(HttpEntity::getBody)
                .map(v -> v.get(key))
                .orElse(emptyList())
                .stream();
    }

    private static Predicate<? super HttpEntity<?>> mediaType(String type) {
        return e -> e.getHeaders().getFirst(CONTENT_TYPE).equals(type);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [payload=" + payload + "]";
    }
}
