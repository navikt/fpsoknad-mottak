package no.nav.foreldrepenger.mottak.domain.felles;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Opphold;

@Data
public class TidligereOppholdsInformasjon {

    private final boolean boddINorge;
    private final ArbeidsInformasjon arbeidsInfo;
    @Opphold(fortid = true)
    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    @JsonCreator
    public TidligereOppholdsInformasjon(@JsonProperty("boddINorge") boolean boddINorge,
            @JsonProperty("arbeidsInfo") ArbeidsInformasjon arbeidsInfo,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.boddINorge = boddINorge;
        this.arbeidsInfo = arbeidsInfo;
        this.utenlandsOpphold = Optional.ofNullable(utenlandsOpphold).orElse(emptyList());
    }

    public boolean isBoddINorge() {
        return utenlandsOpphold.isEmpty();
    }

    public boolean varUtenlands(LocalDate dato) {
        return utenlandsOpphold
                .stream()
                .anyMatch(s -> s.getVarighet().isWithinPeriod(dato));
    }
}
