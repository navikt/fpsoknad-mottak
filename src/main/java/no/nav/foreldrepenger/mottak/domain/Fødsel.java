package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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

    private final List<@Past LocalDate> fødselsdato;

    public Fødsel(LocalDate fødselsdato) {
        this(1, Collections.singletonList(fødselsdato));
    }

    @JsonCreator
    public Fødsel(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("fødselsdato") List<LocalDate> fødselsdato) {
        super(antallBarn);
        this.fødselsdato = fødselsdato(antallBarn, fødselsdato);
    }

    private static List<LocalDate> fødselsdato(int antallBarn, List<LocalDate> fødselsdato) {
        if (fødselsdato.size() == antallBarn) {
            return fødselsdato;
        }
        throw new IllegalStateException("Forventet " + antallBarn + " fødselsdatoer, fikk " + fødselsdato.size());
    }
}
