package no.nav.foreldrepenger.mottak.innsyn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.domain.Søknad;

public class InnsynsSøknad {

    private final SøknadMetadata metadata;
    private final Søknad søknad;

    @JsonCreator
    public InnsynsSøknad(@JsonProperty("metadata") SøknadMetadata metadata, @JsonProperty("søknad") Søknad søknad) {
        this.metadata = metadata;
        this.søknad = søknad;
    }

    public Søknad getSøknad() {
        return søknad;
    }

    public SøknadMetadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [metadata=" + metadata + ", søknad=" + søknad + "]";
    }
}
