package no.nav.foreldrepenger.mottak.domain.felles;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
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
    @Opphold(fortid = false)
    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    @JsonCreator
    public FramtidigOppholdsInformasjon(@JsonProperty("fødseINorge") boolean fødselNorge,
            @JsonProperty("norgeNeste12") boolean norgeNeste12,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.fødselNorge = fødselNorge;
        this.utenlandsOpphold = Optional.ofNullable(utenlandsOpphold).orElse(emptyList());
    }

    public boolean isNorgeNeste12() {
        return utenlandsOpphold.isEmpty();
    }

    public boolean skalVæreUtenlands(LocalDate dato) {
        return utenlandsOpphold
                .stream()
                .anyMatch(s -> s.getVarighet().isWithinPeriod(dato));
    }
}
