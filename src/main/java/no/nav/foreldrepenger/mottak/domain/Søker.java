package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;

import lombok.Data;

@Data
public class Søker {
    private final AktorId aktorid;
    private final BrukerRolle søknadsRolle;
    private final Fullmektig fullmektig;

    public Søker(AktorId aktorId, BrukerRolle brukerRolle) {
        this(aktorId, brukerRolle, null);
    }

    @ConstructorProperties({ "aktorId", "søknadsRolle", "fullmektig" })
    public Søker(AktorId aktorId, BrukerRolle søknadsRolle, Fullmektig fullmektig) {
        this.aktorid = aktorId;
        this.søknadsRolle = søknadsRolle;
        this.fullmektig = fullmektig;
    }
}
