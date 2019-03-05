package no.nav.foreldrepenger.mottak.domain.felles;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Opphold;

@Data
@Valid
public class FramtidigOppholdsInformasjon {

    @Opphold
    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    @JsonCreator
    public FramtidigOppholdsInformasjon(@JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.utenlandsOpphold = Optional.ofNullable(utenlandsOpphold).orElse(emptyList());
    }

    @JsonIgnore
    public boolean isNorgeNeste12() {
        return utenlandsOpphold.isEmpty();
    }
}
