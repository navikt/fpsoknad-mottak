package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class PDLPerson {
    private final List<PDLNavn> navn;
    private final List<PDLKjønn> kjønn;
    private final List<PDLStatsborgerskap> statsborgerskap;
    private final List<LocalDate> fødselsdato;

    @JsonCreator
    public PDLPerson(@JsonProperty("navn") List<PDLNavn> navn, @JsonProperty("kjoenn") List<PDLKjønn> kjønn,
            @JsonProperty("statsborgerskap") List<PDLStatsborgerskap> statsborgerskap, @JsonProperty("foedsel") List<LocalDate> fødselsdato) {
        this.navn = navn;
        this.kjønn = kjønn;
        this.statsborgerskap = statsborgerskap;
        this.fødselsdato = fødselsdato;
    }

    public List<LocalDate> getFødselsdato() {
        return fødselsdato;
    }

    public List<PDLStatsborgerskap> getStatsborgerskap() {
        return statsborgerskap;
    }

    public List<PDLKjønn> getKjønn() {
        return kjønn;
    }

    public List<PDLNavn> getNavn() {
        return navn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [navn=" + navn + ", kjønn=" + kjønn + ", statsborgerskap=" + statsborgerskap + ", fødselsdato="
                + fødselsdato + "]";
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

    static class PDLStatsborgerskap {
        private final CountryCode land;

        @JsonCreator
        public PDLStatsborgerskap(@JsonProperty("land") String land) {
            this.land = CountryCode.getByAlpha3Code(land);
        }

        public CountryCode getLand() {
            return land;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [land=" + land + "]";
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
