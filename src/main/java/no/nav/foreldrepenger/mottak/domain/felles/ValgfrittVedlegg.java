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
public class ValgfrittVedlegg extends Vedlegg {

    ValgfrittVedlegg(DokumentType dokumentType, Resource vedlegg) throws IOException {
        this(dokumentType.dokumentTypeId, dokumentType, vedlegg);
    }

    ValgfrittVedlegg(String id, DokumentType dokumentType, Resource vedlegg) throws IOException {
        this(null, id, dokumentType, vedlegg);
    }

    ValgfrittVedlegg(String beskrivelse, String id, DokumentType dokumentType, Resource vedlegg) throws IOException {
        this(new VedleggMetaData(beskrivelse, id, dokumentType), vedlegg);
    }

    ValgfrittVedlegg(VedleggMetaData metadata, Resource vedlegg) throws IOException {
        super(metadata, vedlegg);
    }

    ValgfrittVedlegg(VedleggMetaData metadata, InputStream inputStream) throws IOException {
        super(metadata, inputStream);
    }

    @JsonCreator
    public ValgfrittVedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg)
            throws IOException {
        super(metadata, vedlegg);
    }

}
