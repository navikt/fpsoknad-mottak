package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FPSakFordeltKvittering extends FPFordelKvittering {

    static final String STATUS = "FPSAK";

    private final String jounalId;
    private final String saksnummer;

    public String getJounalId() {
        return jounalId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    @JsonCreator
    public FPSakFordeltKvittering(@JsonProperty("jounalId") String jounalId,
            @JsonProperty("saksnummer") String saksnummer) {
        super(STATUS);
        this.jounalId = jounalId;
        this.saksnummer = saksnummer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jounalId, saksnummer);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FPSakFordeltKvittering other = (FPSakFordeltKvittering) obj;
        if (jounalId == null) {
            if (other.jounalId != null) {
                return false;
            }
        }
        else if (!jounalId.equals(other.jounalId)) {
            return false;
        }
        if (saksnummer == null) {
            if (other.saksnummer != null) {
                return false;
            }
        }
        else if (!saksnummer.equals(other.saksnummer)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [jounalId=" + jounalId + ", saksnummer=" + saksnummer + "]";
    }
}
