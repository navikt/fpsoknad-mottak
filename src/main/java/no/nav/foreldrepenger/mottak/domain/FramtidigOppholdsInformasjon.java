package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FramtidigOppholdsInformasjon {

    private final boolean fødseINorge;
    private final boolean norgeNeste12;

    @JsonCreator
    public FramtidigOppholdsInformasjon(@JsonProperty("fødseINorge") boolean fødseINorge,
            @JsonProperty("norgeNeste12") boolean norgeNeste12) {
        this.fødseINorge = fødseINorge;
        this.norgeNeste12 = norgeNeste12;
    }
}
