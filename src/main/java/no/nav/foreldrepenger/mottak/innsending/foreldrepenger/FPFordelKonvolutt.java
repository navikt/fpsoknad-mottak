package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.HOVEDDOKUMENT;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.METADATA;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator.VEDLEGG;
import static org.springframework.http.MediaType.APPLICATION_XML;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

public class FPFordelKonvolutt<T> {
    private final HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload;

    public FPFordelKonvolutt(HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        this.payload = payload;
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> getPayload() {
        return payload;
    }

    public List<HttpEntity<?>> getMetadata() {
        return payload.getBody().get(METADATA);
    }

    public List<HttpEntity<?>> getVedlegg() {
        return payload.getBody().get(VEDLEGG);
    }

    public List<HttpEntity<?>> getHoveddokumenter() {
        return payload.getBody().get(HOVEDDOKUMENT);
    }

    public HttpEntity<?> getXMLDokument() {
        return getHoveddokumenter().stream()
                .filter(mediaType(APPLICATION_XML))
                .findFirst()
                .orElse(null);
    }

    private static Predicate<? super HttpEntity<?>> mediaType(MediaType type) {
        return e -> e.getHeaders().getContentType().equals(type);
    }

    public int antallElementer() {
        return payload.getBody().size();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [payload=" + payload + "]";
    }
}
