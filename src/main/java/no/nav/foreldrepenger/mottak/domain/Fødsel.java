package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.BarnOgFødselsdatoer;
import no.nav.foreldrepenger.mottak.domain.validation.PastOrToday;

import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@BarnOgFødselsdatoer
public class Fødsel extends RelasjonTilBarn {

    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.fødsel.fødselsdato}") LocalDate> fødselsdato;

    public Fødsel(LocalDate fødselsdato) {
        this(1, Collections.singletonList(fødselsdato));
    }

    @JsonCreator
    public Fødsel(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("fødselsdato") List<LocalDate> fødselsdato) {
        super(antallBarn);
        this.fødselsdato = fødselsdato;
    }
}
