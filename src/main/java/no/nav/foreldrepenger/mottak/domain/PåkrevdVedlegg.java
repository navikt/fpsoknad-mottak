package no.nav.foreldrepenger.mottak.domain;

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

    public PåkrevdVedlegg(Skjemanummer skjemanummer, Resource vedlegg) throws IOException {
        this(skjemanummer.getBeskrivelse(), skjemanummer, vedlegg);
    }

    public PåkrevdVedlegg(String beskrivelse, Skjemanummer skjemanummer, Resource vedlegg) throws IOException {
        this(new VedleggMetaData(beskrivelse, skjemanummer), vedlegg);
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
