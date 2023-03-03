package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.SøknadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.KonvoluttGenerator.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.*;

public class Konvolutt {
    private final SøknadEgenskap egenskap;
    private final MultiValueMap<String, HttpEntity<?>> payload;
    private final List<String> opplastedeVedlegg;
    private final List<String> ikkeOpplastedeVedlegg;
    private static final Logger LOG = LoggerFactory.getLogger(Konvolutt.class);
    private final Object innsending;
    private final LocalDateTime opprettet;

    public Konvolutt(SøknadEgenskap egenskap, Object innsending, MultiValueMap<String, HttpEntity<?>> payload,
            List<String> opplastedeVedlegg, List<String> ikkeOpplastedeVedlegg) {
        this.egenskap = egenskap;
        this.innsending = innsending;
        this.payload = payload;
        this.opplastedeVedlegg = Optional.ofNullable(opplastedeVedlegg).orElse(emptyList());
        this.ikkeOpplastedeVedlegg = Optional.ofNullable(ikkeOpplastedeVedlegg).orElse(emptyList());
        this.opprettet = LocalDateTime.now();
        LOG.trace("Konvolutt er {}", this);
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    public List<String> getOpplastedeVedlegg() {
        return opplastedeVedlegg;
    }

    public List<String> getIkkeOpplastedeVedlegg() {
        return ikkeOpplastedeVedlegg;
    }

    public boolean erInitiellForeldrepenger() {
        return getEgenskap().erInitiellForeldrepenger();
    }

    public boolean erEndring() {
        return getEgenskap().erEndring();
    }

    public boolean erEttersending() {
        return getEgenskap().erEttersending();
    }

    public SøknadEgenskap getEgenskap() {
        return egenskap;
    }

    public SøknadType getType() {
        return getEgenskap().getType();
    }

    public Object getInnsending() {
        return innsending;
    }

    MultiValueMap<String, HttpEntity<?>> getPayload() {
        return payload;
    }

    String getMetadata() {
        return get(METADATA)
                .filter(mediaType(APPLICATION_JSON_VALUE))
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
                .toList();
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
                .map(v -> v.get(key))
                .orElse(emptyList())
                .stream();
    }

    private static Predicate<? super HttpEntity<?>> mediaType(String type) {
        return e -> type.equals(e.getHeaders().getFirst(CONTENT_TYPE));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[egenskap=" + egenskap + ", payload=" + payload + ", opplastedeVedlegg="
                + opplastedeVedlegg + ", ikkeOpplastedeVedlegg=" + ikkeOpplastedeVedlegg + ", innsending=" + innsending
                + "]";
    }

}
