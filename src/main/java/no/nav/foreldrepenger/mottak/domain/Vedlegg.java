package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = ValgfrittVedlegg.class, name = "valgfritt"),
        @Type(value = PåkrevdVedlegg.class, name = "påkrevd")
})
public abstract class Vedlegg {

    private final VedleggMetaData metadata;
    private final byte[] vedlegg;

    public Vedlegg(VedleggMetaData metadata, Resource vedlegg) throws IOException {
        this(metadata, vedlegg.getInputStream());
    }

    public Vedlegg(VedleggMetaData metadata, InputStream inputStream) throws IOException {
        this(metadata, copyToByteArray(inputStream));
    }

    @JsonCreator
    public Vedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg)
            throws IOException {
        this.metadata = metadata;
        this.vedlegg = vedlegg;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "metadata=" + metadata + "vedlegg=" + bytes();
    }

    private String bytes() {
        if (vedlegg.length > 50) {
            return Arrays.toString(vedlegg).substring(0, 49) + ".... " + (vedlegg.length - 50) + " more bytes";
        }
        return Arrays.toString(vedlegg);
    }
}
