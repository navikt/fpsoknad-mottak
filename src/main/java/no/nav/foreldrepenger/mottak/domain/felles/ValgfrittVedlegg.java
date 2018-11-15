package no.nav.foreldrepenger.mottak.domain.felles;

import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;

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

    public ValgfrittVedlegg(String id, InnsendingsType innsendingsType, DokumentType dokumentType, Resource vedlegg) {
        this(new VedleggMetaData(id, innsendingsType, dokumentType),
                innsendingsType.equals(LASTET_OPP) ? bytesFra(vedlegg) : null);
    }

    @JsonCreator
    public ValgfrittVedlegg(@JsonProperty("metadata") VedleggMetaData metadata,
            @JsonProperty("vedlegg") byte[] vedlegg) {
        super(metadata, vedlegg);
    }

}
