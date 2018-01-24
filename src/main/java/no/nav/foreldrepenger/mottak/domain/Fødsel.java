package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Fødsel extends RelasjonTilBarn {

    private final LocalDate fødselsdato;

    public Fødsel(LocalDate fødselsdato) {
        this(1, fødselsdato);
    }

    @ConstructorProperties({ "antallBarn", "fødselsdato" })
    public Fødsel(int antallBarn, LocalDate fødselsdato) {
        super(antallBarn);
        this.fødselsdato = fødselsdato;
    }
}
