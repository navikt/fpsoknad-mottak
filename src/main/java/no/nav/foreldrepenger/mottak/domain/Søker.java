package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.oppslag.dkif.Målform.standard;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.oppslag.dkif.Målform;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Søker(@NotNull(message = "{ytelse.søknadsrolle.notnull}") BrukerRolle søknadsRolle,
        @JsonProperty("språkkode") Målform målform) {
    public Målform getMålform() {
        return Optional.ofNullable(målform).orElse(standard());
    }
}
