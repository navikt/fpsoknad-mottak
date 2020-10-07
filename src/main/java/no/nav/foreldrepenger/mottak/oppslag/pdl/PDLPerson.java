package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PDLPerson {
    public final List<PDLNavn> navn;

    @JsonCreator
    public PDLPerson(@JsonProperty("navn") List<PDLNavn> navn) {
        this.navn = navn;
    }

    public List<PDLNavn> getNavn() {
        return navn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [navn=" + navn + "]";
    }

    static class PDLNavn {
        private final String fornavn;
        private final String mellomnavn;
        private final String etternavn;

        // @JsonCreator
        public PDLNavn(/* @JsonProperty("fornavn") */ String fornavn, /* @JsonProperty("mellomnavn") */String mellomnavn,
                /* @JsonProperty("etternavn") */ String etternavn) {
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

}
