package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FPFordelGosysKvittering extends FPFordelKvittering {

    static final String STATUS = "Manuell journalføring";

    private final String journalId;

    @JsonCreator
    public FPFordelGosysKvittering(@JsonProperty("journalId") String journalId) {
        super(STATUS);
        this.journalId = journalId;
    }

    public String getJournalId() {
        return journalId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalId, getforsendelseStatus());
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
        if (journalId == null) {
            if (other.journalId != null) {
                return false;
            }
        }
        else if (!journalId.equals(other.journalId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [journalId=" + journalId + ", status=" + getforsendelseStatus() + "]";
    }

}
