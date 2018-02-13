package no.nav.foreldrepenger.mottak.domain;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FramtidigOppholdsInformasjon {

    private final boolean fødseINorge;
    private final boolean norgeNeste12;
    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    public FramtidigOppholdsInformasjon(boolean fødseINorge, List<Utenlandsopphold> utenlandsOpphold) {
        this(fødseINorge, utenlandsOpphold.isEmpty(), utenlandsOpphold);
    }

    @JsonCreator
    public FramtidigOppholdsInformasjon(@JsonProperty("fødseINorge") boolean fødseINorge,
            @JsonProperty("norgeNeste12") boolean norgeNeste12,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.fødseINorge = fødseINorge;
        this.norgeNeste12 = norgeNeste12;
        this.utenlandsOpphold = utenlandsOpphold != null ? utenlandsOpphold : Collections.emptyList();
    }
}
