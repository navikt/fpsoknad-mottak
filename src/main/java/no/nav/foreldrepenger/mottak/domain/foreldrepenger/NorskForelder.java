package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.AktorId;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NorskForelder extends AnnenForelder {

    private final AktorId aktørId;

    @JsonCreator
    public NorskForelder(AktorId aktørId) {
        this.aktørId = aktørId;
    }

}
