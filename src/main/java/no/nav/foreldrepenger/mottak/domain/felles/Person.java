package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;

@JsonInclude(NON_NULL)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
    private final Fødselsnummer fnr;
    private final Navn navn;
    private final LocalDate fødselsdato;
    private final String målform;
    private final CountryCode land;
    private final Boolean ikkeNordiskEøsLand;
    private final Bankkonto bankkonto;
    private AktørId aktørId;

    @JsonCreator
    public Person(@JsonProperty("fnr") Fødselsnummer fnr,
            @JsonProperty("navn") Navn navn,
            @JsonProperty("fødselsdato") LocalDate fødselsdato,
            @JsonProperty("målform") String målform,
            @JsonProperty("land") CountryCode land,
            @JsonProperty("ikkeNordiskEøsLand") Boolean ikkeNordiskEøsLand,
            @JsonProperty("bankkonto") Bankkonto bankkonto) {
        this.fnr = fnr;
        this.navn = navn;
        this.fødselsdato = fødselsdato;
        this.målform = målform;
        this.land = Optional.ofNullable(land).orElse(CountryCode.NO);
        this.ikkeNordiskEøsLand = ikkeNordiskEøsLand;
        this.bankkonto = bankkonto;
    }

    public String getFornavn() {
        return Optional.ofNullable(navn).map(Navn::getFornavn).orElse(null);
    }

    public String getMellomnavn() {
        return Optional.ofNullable(navn).map(Navn::getMellomnavn).orElse(null);
    }

    public String getEtternavn() {
        return Optional.ofNullable(navn).map(Navn::getEtternavn).orElse(null);
    }

    public Kjønn getKjønn() {
        return Optional.ofNullable(navn).map(Navn::getKjønn).orElse(Kjønn.U);

    }
}