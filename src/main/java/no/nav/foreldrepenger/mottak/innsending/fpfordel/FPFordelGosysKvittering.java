package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FPFordelGosysKvittering extends FPFordelKvittering {

    static final String STATUS = "Manuell journalføring";

    private final String jounalId;

    @JsonCreator
    public FPFordelGosysKvittering(@JsonProperty("jounalId") String jounalId) {
        super(STATUS);
        this.jounalId = jounalId;
    }

    public String getJounalId() {
        return jounalId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jounalId, getforsendelseStatus());
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
        FPFordelGosysKvittering other = (FPFordelGosysKvittering) obj;
        if (jounalId == null) {
            if (other.jounalId != null) {
                return false;
            }
        }
        else if (!jounalId.equals(other.jounalId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [jounalId=" + jounalId + ", status=" + getforsendelseStatus() + "]";
    }

}
