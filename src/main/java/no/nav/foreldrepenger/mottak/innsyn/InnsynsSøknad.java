package no.nav.foreldrepenger.mottak.innsyn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class InnsynsSøknad {

    private final Versjon versjon;
    private final Søknad søknad;
    private final String journalpostId;

    @JsonCreator
    public InnsynsSøknad(@JsonProperty("versjon") Versjon versjon, @JsonProperty("søknad") Søknad søknad,
            @JsonProperty("journalpostId") String journalpostId) {
        this.versjon = versjon;
        this.søknad = søknad;
        this.journalpostId = journalpostId;
    }

    public Versjon getVersjon() {
        return versjon;
    }

    public Søknad getSøknad() {
        return søknad;
    }

    public String getJournalpostId() {
        return journalpostId;
    }
}
