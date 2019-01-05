package no.nav.foreldrepenger.mottak.innsyn;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.SøknadEgenskaper;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class SøknadMetadata {

    private final SøknadEgenskaper egenskaper;
    private final String journalpostId;

    @JsonCreator
    public SøknadMetadata(@JsonProperty("resultat") SøknadEgenskaper egenskaper,
            @JsonProperty("journalpostId") String journalpostId) {
        this.egenskaper = egenskaper;
        this.journalpostId = journalpostId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(egenskaper, journalpostId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SøknadMetadata other = (SøknadMetadata) obj;
        if (journalpostId == null) {
            if (other.journalpostId != null)
                return false;
        }
        else if (!journalpostId.equals(other.journalpostId))
            return false;
        if (egenskaper == null) {
            if (other.egenskaper != null)
                return false;
        }
        else if (!egenskaper.equals(other.egenskaper))
            return false;
        return true;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    @JsonIgnore
    public Versjon getVersjon() {
        return egenskaper.getVersjon();
    }

    @JsonIgnore
    public SøknadType getType() {
        return egenskaper.getType();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [egenskaper=" + egenskaper + ", journalpostId=" + journalpostId + "]";
    }

}
