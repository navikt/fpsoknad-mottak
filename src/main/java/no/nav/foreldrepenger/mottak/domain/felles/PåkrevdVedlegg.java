package no.nav.foreldrepenger.mottak.domain.felles;

import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.IOException;

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

    PåkrevdVedlegg(String id, DokumentType dokumentType, Resource vedlegg) throws IOException {
        this(new VedleggMetaData(id, LASTET_OPP, dokumentType), copyToByteArray(vedlegg.getInputStream()));
    }

    @JsonCreator
    public PåkrevdVedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg)
            throws IOException {
        super(metadata, vedlegg);
    }
}
