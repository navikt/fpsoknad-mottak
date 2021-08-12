package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.BarnDTO;

@JsonInclude(NON_NULL)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
    private final Fødselsnummer fnr;
    private final Navn navn;
    private final LocalDate fødselsdato;
    @Exclude
    private final String målform;
    private final CountryCode land;
    private final Bankkonto bankkonto;
    private AktørId aktørId;
    private final Set<BarnDTO> barn;

    @JsonCreator
    public Person(@JsonProperty("fnr") Fødselsnummer fnr,
            @JsonProperty("navn") Navn navn,
            @JsonProperty("fødselsdato") LocalDate fødselsdato,
            @JsonProperty("målform") String målform,
            @JsonProperty("land") CountryCode land,
            @JsonProperty("bankkonto") Bankkonto bankkonto, Set<BarnDTO> barn) {
        this.fnr = fnr;
        this.navn = navn;
        this.fødselsdato = fødselsdato;
        this.målform = Optional.ofNullable(målform).orElse(Målform.standard().name());
        this.land = Optional.ofNullable(land).orElse(CountryCode.NO);
        this.bankkonto = bankkonto;
        this.barn = barn;
    }

    public String getFornavn() {
        return Optional.ofNullable(navn)
                .map(Navn::fornavn)
                .orElse(null);
    }

    public String getMellomnavn() {
        return Optional.ofNullable(navn)
                .map(Navn::mellomnavn)
                .orElse(null);
    }

    public String getEtternavn() {
        return Optional.ofNullable(navn)
                .map(Navn::etternavn)
                .orElse(null);
    }

    public Kjønn getKjønn() {
        return Optional.ofNullable(navn)
                .map(Navn::kjønn)
                .orElse(Kjønn.U);

    }
}