package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final VedleggType type;

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse, @JsonProperty("type") VedleggType type) {
        this.beskrivelse = beskrivelse;
        this.type = type;
    }

}
