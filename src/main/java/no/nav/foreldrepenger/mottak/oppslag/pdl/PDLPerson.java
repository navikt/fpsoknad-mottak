package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PDLPerson {
    private final List<PDLNavn> navn;
    private final List<PDLKjønn> kjønn;

    @JsonCreator
    public PDLPerson(@JsonProperty("navn") List<PDLNavn> navn, @JsonProperty("kjoenn") List<PDLKjønn> kjønn) {
        this.navn = navn;
        this.kjønn = kjønn;
    }

    public List<PDLKjønn> getKjønn() {
        return kjønn;
    }

    public List<PDLNavn> getNavn() {
        return navn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [navn=" + navn + ", kjønn=" + kjønn + "]";
    }

    static class PDLNavn {
        private final String fornavn;
        private final String mellomnavn;
        private final String etternavn;

        @JsonCreator
        public PDLNavn(@JsonProperty("fornavn") String fornavn, @JsonProperty("mellomnavn") String mellomnavn,
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

    static class PDLKjønn {
        private final Kjønn kjønn;

        @JsonCreator
        public PDLKjønn(@JsonProperty("kjoenn") Kjønn kjønn) {
            this.kjønn = kjønn;
        }

        public Kjønn getKjønn() {
            return kjønn;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [kjønn=" + kjønn + "]";
        }

        static enum Kjønn {
            MANN,
            KVINNE,
            UKJENT
        }
    }

}
