package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Fødsel extends RelasjonTilBarn {

    @Past
    private final LocalDate fødselsdato;

    public Fødsel(LocalDate fødselsdato) {
        this(1, fødselsdato);
    }

    @JsonCreator
    public Fødsel(@JsonProperty("antallBarn") int antallBarn, @JsonProperty("fødselsdato") LocalDate fødselsdato) {
        super(antallBarn);
        this.fødselsdato = fødselsdato;
    }
}
