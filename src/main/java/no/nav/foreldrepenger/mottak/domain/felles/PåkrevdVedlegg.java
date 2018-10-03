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

    private PåkrevdVedlegg(DokumentType dokumentType, Resource vedlegg) throws IOException {
        this(dokumentType.name(), dokumentType, vedlegg);
    }

    PåkrevdVedlegg(String id, DokumentType dokumentType, Resource vedlegg) throws IOException {
        this(null, id, dokumentType, vedlegg);
    }

    PåkrevdVedlegg(String beskrivelse, String id, DokumentType dokumentType, Resource vedlegg) throws IOException {
        this(new VedleggMetaData(beskrivelse, id, dokumentType), vedlegg);
    }

    PåkrevdVedlegg(VedleggMetaData metadata, Resource vedlegg) throws IOException {
        super(metadata, vedlegg);
    }

    PåkrevdVedlegg(VedleggMetaData metadata, InputStream inputStream) throws IOException {
        super(metadata, inputStream);
    }

    @JsonCreator
    public PåkrevdVedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg)
            throws IOException {
        super(metadata, vedlegg);
    }
}
