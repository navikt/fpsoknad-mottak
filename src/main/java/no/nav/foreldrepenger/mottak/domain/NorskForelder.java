package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public final class NorskForelder extends KjentForelder {

    private final AktorId aktørId;

    @JsonCreator
    public NorskForelder(@JsonProperty("lever") boolean lever, @JsonProperty("aktorId") AktorId aktorId) {
        super(lever);
        this.aktørId = aktorId;
    }

}
