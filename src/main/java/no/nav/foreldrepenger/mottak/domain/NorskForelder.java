package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class NorskForelder extends KjentForelder {

    private final AktorId aktørId;

    @Builder
    @ConstructorProperties({ "lever", "aktorId" })
    public NorskForelder(boolean lever, AktorId aktorId) {
        super(lever);
        this.aktørId = aktorId;
    }

}
