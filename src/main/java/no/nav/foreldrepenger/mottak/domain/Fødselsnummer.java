package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.util.StringUtil.partialMask;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;

@Data
public class Fødselsnummer {

    @JsonValue
    private final String fnr;

    public Fødselsnummer(String fnr) {
        this.fnr = fnr;
    }

    public static Fødselsnummer valueOf(String fnr) {
        return new Fødselsnummer(fnr);
    }

    public Kjønn kjønn() {
        if (fnr != null & fnr.length() == 11) {
            return Integer.valueOf(fnr.charAt(8)) % 2 == 0 ? Kjønn.K : Kjønn.M;
        }
        return Kjønn.U;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + partialMask(fnr) + "]";
    }
}
