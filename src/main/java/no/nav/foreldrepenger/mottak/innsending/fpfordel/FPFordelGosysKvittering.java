package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FPFordelGosysKvittering extends FPFordelKvittering {

    static final String STATUS = "Manuell journalføring";

    private final String journalpostId;

    public String getJournalpostId() {
        return journalpostId;
    }

    @JsonCreator
    public FPFordelGosysKvittering(@JsonProperty("journalpostId") String journalpostId) {
        super(STATUS);
        this.journalpostId = journalpostId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalpostId, getStatus());
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
        if (journalpostId == null) {
            if (other.journalpostId != null) {
                return false;
            }
        }
        else if (!journalpostId.equals(other.journalpostId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [journalpostId=" + journalpostId + ", status=" + getStatus() + "]";
    }

}
