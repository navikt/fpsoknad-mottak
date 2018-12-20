package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class InnsynsSøknad {

    private final Versjon versjon;
    private final Søknad søknad;
    private final String journalpostId;
    private final SøknadType type;

    @JsonCreator
    public InnsynsSøknad(@JsonProperty("versjon") Versjon versjon, @JsonProperty("søknad") Søknad søknad,
            @JsonProperty("journalpostId") String journalpostId) {
        this(versjon, søknad, journalpostId, typeFra(søknad));
    }

    public InnsynsSøknad(Versjon versjon, Søknad søknad, String journalpostId, SøknadType type) {
        this.versjon = versjon;
        this.søknad = søknad;
        this.journalpostId = journalpostId;
        this.type = type;
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

    public SøknadType getType() {
        return type;
    }

    private static SøknadType typeFra(Søknad søknad) {
        return søknad instanceof Endringssøknad ? ENDRING : INITIELL;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [versjon=" + versjon + ", søknad=" + søknad + ", journalpostId="
                + journalpostId + ", type=" + type + "]";
    }
}
