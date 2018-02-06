package no.nav.foreldrepenger.mottak.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Søker {
    @NotNull(message = "{ytelse.aktørid.notnull}")
    private final AktorId aktorid;
    @NotNull(message = "{ytelse.søknadsrolle.notnull}")
    private final BrukerRolle søknadsRolle;
    private final Fullmektig fullmektig;

    public Søker(AktorId aktorId, BrukerRolle brukerRolle) {
        this(aktorId, brukerRolle, null);
    }

    @JsonCreator
    public Søker(@JsonProperty("aktorId") AktorId aktorId, @JsonProperty("søknadsRolle") BrukerRolle søknadsRolle,
            @JsonProperty("fullmektig") Fullmektig fullmektig) {
        this.aktorid = aktorId;
        this.søknadsRolle = søknadsRolle;
        this.fullmektig = fullmektig;
    }
}
