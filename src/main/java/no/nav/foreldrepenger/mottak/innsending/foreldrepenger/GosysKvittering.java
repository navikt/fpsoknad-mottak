package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GosysKvittering extends FordelKvittering {

    static final String STATUS = "GOSYS";

    private final String journalpostId;

    @JsonCreator
    public GosysKvittering(@JsonProperty("journalpostId") String journalpostId) {
        super(STATUS);
        this.journalpostId = journalpostId;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalpostId, getforsendelseStatus());
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
        GosysKvittering other = (GosysKvittering) obj;
        if (journalpostId == null) {
            if (other.journalpostId != null) {
                return false;
            }
        } else if (!journalpostId.equals(other.journalpostId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [journalpostId=" + journalpostId + ", status=" + getforsendelseStatus()
                + "]";
    }

}
