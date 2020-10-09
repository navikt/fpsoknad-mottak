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
    private final List<PDLFødselsdato> fødselsdato;
    private final List<PDLFamilierelasjon> familierelasjoner;
    private final List<PDLSivilstand> sivilstand;

    @JsonCreator
    public PDLPerson(@JsonProperty("navn") List<PDLNavn> navn,
            @JsonProperty("kjoenn") List<PDLKjønn> kjønn,
            @JsonProperty("statsborgerskap") List<PDLStatsborgerskap> statsborgerskap,
            @JsonProperty("foedsel") List<PDLFødselsdato> fødselsdato,
            @JsonProperty("familierelasjoner") List<PDLFamilierelasjon> familierelasjoner,
            @JsonProperty("sivilstand") List<PDLSivilstand> sivilstand) {
        this.navn = navn;
        this.kjønn = kjønn;
        this.statsborgerskap = statsborgerskap;
        this.fødselsdato = fødselsdato;
        this.familierelasjoner = familierelasjoner;
        this.sivilstand = sivilstand;
    }

    public List<PDLSivilstand> getSivilstand() {
        return sivilstand;
    }

    public List<PDLFamilierelasjon> getFamilierelasjoner() {
        return familierelasjoner;
    }

    public List<PDLFødselsdato> getFødselsdato() {
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
                + fødselsdato + ", sivilstand=" + sivilstand + ", familierelasjoner=" + familierelasjoner + "]";
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

    static class PDLSivilstand {

        private final PDLSivilstandType type;
        private final String relatertVedSivilstand;

        @JsonCreator
        public PDLSivilstand(@JsonProperty("type") PDLSivilstandType type, @JsonProperty("relatertVedSivilstand") String relatertVedSivilstand) {
            this.type = type;
            this.relatertVedSivilstand = relatertVedSivilstand;
        }

        public PDLSivilstandType getType() {
            return type;
        }

        public String getRelatertVedSivilstand() {
            return relatertVedSivilstand;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [type=" + type + ", relatertVedSivilstand=" + relatertVedSivilstand + "]";
        }

        static enum PDLSivilstandType {
            UOPPGITT,
            UGIFT,
            GIFT,
            ENKE_ELLER_ENKEMANN,
            SKILT,
            SEPARERT,
            REGISTRERT_PARTNER,
            SEPARERT_PARTNER,
            SKILT_PARTNER,
            GJENLEVENDE_PARTNER
        }
    }

    static class PDLFamilierelasjon {

        private final String id;
        private final PDLRelasjonsRolle relatertPersonrolle;
        private final PDLRelasjonsRolle minRolle;

        @JsonCreator
        public PDLFamilierelasjon(@JsonProperty("relatertPersonsIdent") String id,
                @JsonProperty("relatertPersonsRolle") PDLRelasjonsRolle relatertPersonrolle,
                @JsonProperty("minRolleForPerson") PDLRelasjonsRolle minRolle) {
            this.id = id;
            this.relatertPersonrolle = relatertPersonrolle;
            this.minRolle = minRolle;
        }

        public String getId() {
            return id;
        }

        public PDLRelasjonsRolle getRelatertPersonrolle() {
            return relatertPersonrolle;
        }

        public PDLRelasjonsRolle getMinRolle() {
            return minRolle;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [id=" + id + ", relatertPersonrolle=" + relatertPersonrolle + ", minRolle=" + minRolle + "]";
        }

        static enum PDLRelasjonsRolle {
            BARN,
            MOR,
            FAR,
            MEDMOR
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
