package no.nav.foreldrepenger.mottak.innsyn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.SøknadInspeksjonResultat;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class SøknadMetadata {

    private final SøknadInspeksjonResultat resultat;
    private final String journalpostId;

    @JsonCreator
    public SøknadMetadata(@JsonProperty("resultat") SøknadInspeksjonResultat resultat,
            @JsonProperty("journalpostId") String journalpostId) {
        this.resultat = resultat;
        this.journalpostId = journalpostId;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    @JsonIgnore
    public Versjon getVersjon() {
        return resultat.versjon();
    }

    @JsonIgnore
    public SøknadType getType() {
        return resultat.type();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [resultat=" + resultat + ", journalpostId=" + journalpostId + "]";
    }

}
