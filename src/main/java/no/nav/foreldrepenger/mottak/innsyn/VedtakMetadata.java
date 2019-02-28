package no.nav.foreldrepenger.mottak.innsyn;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VedtakMetadata {

    private final String journalpostId;

    @JsonCreator
    public VedtakMetadata(@JsonProperty("journalpostId") String journalpostId) {
        this.journalpostId = journalpostId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalpostId);
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
        VedtakMetadata other = (VedtakMetadata) obj;
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

    public String getJournalpostId() {
        return journalpostId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [journalpostId=" + journalpostId + "]";
    }
}
