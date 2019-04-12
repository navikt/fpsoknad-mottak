package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;

@Data
public class ProsentAndel {

    @Prosent
    private final Double prosent;

    @JsonCreator
    public ProsentAndel(@JsonProperty("prosent") Double prosent) {
        this.prosent = prosent;
    }
}
