package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;

import org.apache.commons.compress.utils.IOUtils;
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

    public ValgfrittVedlegg(String description, Resource vedlegg) throws IOException {
        this(description, IOUtils.toByteArray(vedlegg.getInputStream()));
    }

    @JsonCreator
    public ValgfrittVedlegg(@JsonProperty("description") String description,
            @JsonProperty("vedlegg") byte[] vedlegg) {
        super(description, vedlegg);
    }
}
