package no.nav.foreldrepenger.mottak.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Søker {
    @NotNull(message = "{ytelse.bruker.notnull}")
    private final Bruker bruker;
    @NotNull(message = "{ytelse.søknadsrolle.notnull}")
    private final BrukerRolle søknadsRolle;
    private final Fullmektig fullmektig;

    public Søker(Bruker bruker, BrukerRolle brukerRolle) {
        this(bruker, brukerRolle, null);
    }

    @JsonCreator
    public Søker(@JsonProperty("bruker") Bruker bruker, @JsonProperty("søknadsRolle") BrukerRolle søknadsRolle,
            @JsonProperty("fullmektig") Fullmektig fullmektig) {
        this.bruker = bruker;
        this.søknadsRolle = søknadsRolle;
        this.fullmektig = fullmektig;
    }
}
