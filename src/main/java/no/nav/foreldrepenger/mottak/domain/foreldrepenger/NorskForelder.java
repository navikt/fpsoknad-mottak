package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NorskForelder extends AnnenForelder {

    private final Fødselsnummer fnr;

    @JsonCreator
    public NorskForelder(Fødselsnummer fnr) {
        this.fnr = fnr;
    }

}
