package no.nav.foreldrepenger.mottak.domain.felles.medlemskap;

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
public class TidligereOppholdsInformasjon {

    private final ArbeidsInformasjon arbeidSiste12;
    @Opphold(fortid = true)
    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    @JsonCreator
    public TidligereOppholdsInformasjon(
            @JsonProperty("arbeidSiste12") ArbeidsInformasjon arbeidSiste12,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.arbeidSiste12 = arbeidSiste12;
        this.utenlandsOpphold = Optional.ofNullable(utenlandsOpphold).orElse(emptyList());
    }

    @JsonIgnore
    public boolean isBoddINorge() {
        return utenlandsOpphold.isEmpty();
    }
}
