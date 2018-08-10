package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    Vedlegg(VedleggMetaData metadata, Resource vedlegg) throws IOException {
        this(metadata, vedlegg.getInputStream());
    }

    Vedlegg(VedleggMetaData metadata, InputStream inputStream) throws IOException {
        this(metadata, copyToByteArray(inputStream));
    }

    @JsonCreator
    public Vedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg)
            throws IOException {
        this.metadata = metadata;
        this.vedlegg = vedlegg;
    }

    private String bytes() {
        String vedleggAsString = Arrays.toString(vedlegg);
        if (vedleggAsString.length() >= 50) {
            return vedleggAsString.substring(0, 49) + ".... " + (vedleggAsString.length() - 50) + " more bytes";
        }
        return vedleggAsString;
    }

    @JsonIgnore
    public String getBeskrivelse() {
        return metadata.getBeskrivelse();
    }

    @JsonIgnore
    public InnsendingsType getInnsendingsType() {
        return metadata.getInnsendingsType();
    }

    @JsonIgnore
    public String getId() {
        return metadata.getId();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "metadata=" + metadata + "vedlegg=" + bytes();
    }
}
