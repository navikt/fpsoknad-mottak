package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class PDLPerson {
    private final PDLNavn navn;
    private final PDLKjønn kjønn;
    private final PDLStatsborgerskap statsborgerskap;
    private final PDLFødselsdato fødselsdato;

    @JsonCreator
    public PDLPerson(@JsonProperty("navn") PDLNavn navn, @JsonProperty("kjoenn") PDLKjønn kjønn,
            @JsonProperty("statsborgerskap") PDLStatsborgerskap statsborgerskap, @JsonProperty("foedsel") PDLFødselsdato fødselsdato) {
        this.navn = navn;
        this.kjønn = kjønn;
        this.statsborgerskap = statsborgerskap;
        this.fødselsdato = fødselsdato;
    }

    public PDLFødselsdato getFødselsdato() {
        return fødselsdato;
    }

    public PDLStatsborgerskap getStatsborgerskap() {
        return statsborgerskap;
    }

    public PDLKjønn getKjønn() {
        return kjønn;
    }

    public PDLNavn getNavn() {
        return navn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [navn=" + navn + ", kjønn=" + kjønn + ", statsborgerskap=" + statsborgerskap + ", fødselsdato="
                + fødselsdato + "]";
    }

    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
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

    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
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

    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
    static class PDLFødselsdato {
        private final LocalDate fødselsdato;

        @JsonCreator
        public PDLFødselsdato(@JsonProperty("foedselsdato") LocalDate fødselsdato) {
            this.fødselsdato = fødselsdato;
        }

        public LocalDate getFødselsdato() {
            return fødselsdato;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [fødselsdato=" + fødselsdato + "]";
        }
    }

    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
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
