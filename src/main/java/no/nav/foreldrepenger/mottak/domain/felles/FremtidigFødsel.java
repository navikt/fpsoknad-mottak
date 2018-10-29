package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.PastOrToday;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Termindato;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class FremtidigFødsel extends RelasjonTilBarn {
    @Termindato
    private final LocalDate terminDato;
    @PastOrToday(nullable = true)
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
