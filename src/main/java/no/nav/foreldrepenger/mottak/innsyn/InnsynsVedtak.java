package no.nav.foreldrepenger.mottak.innsyn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InnsynsVedtak {

    private final VedtakMetadata metadata;
    private final Vedtak vedtak;

    @JsonCreator
    public InnsynsVedtak(@JsonProperty("metadata") VedtakMetadata metadata, @JsonProperty("søknad") Vedtak vedtak) {
        this.metadata = metadata;
        this.vedtak = vedtak;
    }

    public Vedtak getVedtak() {
        return vedtak;
    }

    public VedtakMetadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [metadata=" + metadata + ", vedtak=" + vedtak + "]";
    }
}
