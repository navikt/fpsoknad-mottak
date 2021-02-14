package no.nav.foreldrepenger.mottak.domain.felles.medlemskap;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.mottak.domain.validation.annotations.Opphold;

@Valid
public record FramtidigOppholdsInformasjon(@Opphold List<Utenlandsopphold> utenlandsOpphold) {

    public List<Utenlandsopphold> getUtenlandsOpphold() {
        return Optional.ofNullable(utenlandsOpphold).orElse(emptyList());
    }

    @JsonIgnore
    public boolean isNorgeNeste12() {
        return utenlandsOpphold.isEmpty();
    }
}
