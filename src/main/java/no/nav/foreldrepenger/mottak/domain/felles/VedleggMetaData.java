package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final String id;

    public VedleggMetaData(String id) {
        this(null, id);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse, @JsonProperty("id") String id) {
        this.beskrivelse = beskrivelse;
        this.id = id;
    }
}
