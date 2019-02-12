package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.felles.SpråkKode.defaultSpråk;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.SpråkKode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Søker {
    @NotNull(message = "{ytelse.søknadsrolle.notnull}")
    private final BrukerRolle søknadsRolle;
    private final SpråkKode språkkode;

    public Søker(@JsonProperty("søknadsRolle") BrukerRolle søknadsRolle) {
        this(søknadsRolle, defaultSpråk());
    }

    @JsonCreator
    public Søker(@JsonProperty("søknadsRolle") BrukerRolle søknadsRolle,
            @JsonProperty("språkkode") SpråkKode språkkode) {
        this.søknadsRolle = søknadsRolle;
        this.språkkode = Optional.ofNullable(språkkode).orElse(defaultSpråk());
    }
}
