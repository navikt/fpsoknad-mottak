package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Søker {
    private final AktorId aktorid;
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
