package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class FremtidigFødsel extends RelasjonTilBarn {
    @NotNull(message = "{ytelse.relasjontilbarn.framtidigfødsel.termindato.notnull}")
    @Future(message = "{ytelse.relasjontilbarn.framtidigfødsel.termindato.fortid}")
    private final LocalDate terminDato;

    @PastOrToday
    @NotNull(message = "{ytelse.relasjontilbarn.framtidigfødsel.terminbekreftelse.notnull}")
    private final LocalDate utstedtDato;

    public FremtidigFødsel(LocalDate terminDato, LocalDate utstedtDato) {
        this(1, terminDato, utstedtDato);
    }

    @JsonCreator
    public FremtidigFødsel(@JsonProperty("antallBarn") int antallBarn, @JsonProperty("terminDato") LocalDate terminDato,
            @JsonProperty("utstedtDato") LocalDate utstedtDato) {
        super(antallBarn);
        this.terminDato = terminDato;
        this.utstedtDato = utstedtDato;
    }
}
