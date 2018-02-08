package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Splitter;

import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
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
        this(metadata, StreamUtils.copyToByteArray(inputStream));
    }

    @JsonCreator
    public Vedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg)
            throws IOException {
        this.metadata = metadata;
        this.vedlegg = vedlegg;
    }

    private static Resource validate(Resource vedlegg) {
        if (!vedlegg.exists()) {
            throw new IllegalArgumentException("Vedlegg " + vedlegg.getDescription() + " ikke funnet");
        }
        return vedlegg;
    }

    private static String fileNameFra(Resource vedlegg) {
        String fileName = vedlegg.getFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("Vedlegg " + vedlegg.getDescription() + " har ikke noe filenavn");
        }
        return fileName;
    }

    private static VedleggType typeFra(String fileName) {
        String extension = Splitter.on(".").splitToList(fileName).get(1).toUpperCase();
        try {
            return VedleggType.valueOf(extension);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Fil " + fileName + " med extension " + extension + " ikke støttet, gyldig typer er "
                            + Arrays.toString(VedleggType.values()));
        }
    }

}
