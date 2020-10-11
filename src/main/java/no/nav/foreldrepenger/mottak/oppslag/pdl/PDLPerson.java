package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

@Data
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

    @Data
    static class PDLBarn {
        private final Fødselsnummer fnr;
        private final Fødselsnummer fnrSøker;
        private final LocalDate fødselsdato;
        private final PDLNavn navn;
        private final PDLAnnenForelder annenForelder;
    }

    @Data
    static class PDLAnnenForelder {
        private final PDLNavn navn;
        private final Fødselsnummer fnr;
        private final LocalDate fødselsdato;
    }

    @Data
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
    }

    @Data
    static class PDLStatsborgerskap {
        private final CountryCode land;

        @JsonCreator
        public PDLStatsborgerskap(@JsonProperty("land") String land) {
            this.land = CountryCode.getByAlpha3Code(land);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PDLFødselsdato {
        private final LocalDate fødselsdato;

        @JsonCreator
        public PDLFødselsdato(@JsonProperty("foedselsdato") LocalDate fødselsdato) {
            this.fødselsdato = fødselsdato;
        }
    }

    @Data
    static class PDLSivilstand {
        private final PDLSivilstandType type;
        private final String relatertVedSivilstand;

        @JsonCreator
        public PDLSivilstand(@JsonProperty("type") PDLSivilstandType type, @JsonProperty("relatertVedSivilstand") String relatertVedSivilstand) {
            this.type = type;
            this.relatertVedSivilstand = relatertVedSivilstand;
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

    @Data
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

        static enum PDLRelasjonsRolle {
            BARN,
            MOR,
            FAR,
            MEDMOR
        }
    }

    @Data
    static class PDLKjønn {
        private final Kjønn kjønn;

        @JsonCreator
        public PDLKjønn(@JsonProperty("kjoenn") Kjønn kjønn) {
            this.kjønn = kjønn;
        }

        static enum Kjønn {
            MANN,
            KVINNE,
            UKJENT
        }
    }
}
