package no.nav.foreldrepenger.mottak.domain.felles;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PåkrevdVedlegg extends Vedlegg {

    public PåkrevdVedlegg(String id, Resource vedlegg) throws IOException {
        this(null, id, vedlegg);
    }

    public PåkrevdVedlegg(String beskrivelse, String id, Resource vedlegg) throws IOException {
        this(new VedleggMetaData(beskrivelse, id), vedlegg);
    }

    public PåkrevdVedlegg(VedleggMetaData metadata, Resource vedlegg) throws IOException {
        super(metadata, vedlegg);
    }

    public PåkrevdVedlegg(VedleggMetaData metadata, InputStream inputStream) throws IOException {
        super(metadata, inputStream);
    }

    @JsonCreator
    public PåkrevdVedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg)
            throws IOException {
        super(metadata, vedlegg);
    }
}
