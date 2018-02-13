package no.nav.foreldrepenger.mottak.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Søker {
    @NotNull(message = "{ytelse.bruker.notnull}")
    private final Fodselsnummer fnr;
    @NotNull(message = "{ytelse.bruker.notnull}")
    private final AktorId aktør;
    @NotNull(message = "{ytelse.søknadsrolle.notnull}")
    private final BrukerRolle søknadsRolle;

    @JsonCreator
    public Søker(@JsonProperty("fnr") Fodselsnummer fnr, @JsonProperty("aktør") AktorId aktør,
            @JsonProperty("søknadsRolle") BrukerRolle søknadsRolle) {
        this.fnr = fnr;
        this.aktør = aktør;
        this.søknadsRolle = søknadsRolle;
    }
}
