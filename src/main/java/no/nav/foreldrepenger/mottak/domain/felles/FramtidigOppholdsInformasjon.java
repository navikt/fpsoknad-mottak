package no.nav.foreldrepenger.mottak.domain.felles;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Opphold;

@Data
@Valid
@JsonPropertyOrder({ "fødselNorge", "norgeNeste12", "utenlandsOpphold" })
public class FramtidigOppholdsInformasjon {

    private final boolean fødselNorge;
    private final boolean norgeNeste12;
    @Opphold(fortid = false)
    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    @JsonCreator
    public FramtidigOppholdsInformasjon(@JsonProperty("fødseINorge") boolean fødselNorge,
            @JsonProperty("norgeNeste12") boolean norgeNeste12,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.fødselNorge = fødselNorge;
        this.norgeNeste12 = norgeNeste12;
        this.utenlandsOpphold = Optional.ofNullable(utenlandsOpphold).orElse(emptyList());
    }
}
