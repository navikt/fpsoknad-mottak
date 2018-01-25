package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OppholdsInformasjon {

    private final boolean fødselINorge;
    private final boolean norgeNeste12;

    @JsonCreator
    public OppholdsInformasjon(@JsonProperty("fødselINorge") boolean fødselINorge,
            @JsonProperty("norgeNeste12") boolean norgeNeste12) {
        this.fødselINorge = fødselINorge;
        this.norgeNeste12 = norgeNeste12;
    }

}
