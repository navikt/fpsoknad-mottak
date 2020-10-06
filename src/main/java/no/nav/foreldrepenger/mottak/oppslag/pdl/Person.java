package no.nav.foreldrepenger.mottak.oppslag.pdl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;

    @JsonCreator
    public Person(@JsonProperty("fornavn") String fornavn, @JsonProperty("mellomnavn") String mellomnavn,
            @JsonProperty("etternavn") String etternavn) {
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fornavn=" + fornavn + ", mellomnavn=" + mellomnavn + ", etternavn=" + etternavn + "]";
    }
}
