package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArbeidsgiverVirksomhet extends Arbeidsgiver {

    @JsonCreator
    public ArbeidsgiverVirksomhet(@JsonProperty("id") String id) {
        super(id);
    }

}
