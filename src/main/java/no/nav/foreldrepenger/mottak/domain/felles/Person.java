package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

@JsonInclude(NON_NULL)
@Data
public class Person {
    private final Fødselsnummer fnr;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final Kjønn kjønn;
    private final LocalDate fødselsdato;
    private final String målform;
    @JsonAlias("landKode")
    private final CountryCode land;
    private final Boolean ikkeNordiskEøsLand;
    private final Bankkonto bankkonto;
    @JsonAlias("aktorId")
    private AktørId aktørId;

    public Person(@JsonProperty("fnr") Fødselsnummer fnr,
            @JsonProperty("fornavn") String fornavn,
            @JsonProperty("mellomnavn") String mellomnavn,
            @JsonProperty("etternavn") String etternavn,
            @JsonProperty("kjønn") Kjønn kjønn,
            @JsonProperty("fødselsdato") LocalDate fødselsdato,
            @JsonProperty("målform") String målform,
            @JsonProperty("land") CountryCode land,
            @JsonProperty("ikkeNordiskEøsLand") Boolean ikkeNordiskEøsLand,
            @JsonProperty("bankkonto") Bankkonto bankkonto) {
        this.fnr = fnr;
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        this.kjønn = kjønn;
        this.fødselsdato = fødselsdato;
        this.målform = målform;
        this.land = land;
        this.ikkeNordiskEøsLand = ikkeNordiskEøsLand;
        this.bankkonto = bankkonto;
    }
}