package no.nav.foreldrepenger.mottak.domain.felles;

import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.BarnOgFødselsdatoer;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@BarnOgFødselsdatoer
public class Fødsel extends RelasjonTilBarn {

    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.fødsel.fødselsdato}") LocalDate> fødselsdato;

    public Fødsel(LocalDate fødselsdato) {
        this(1, singletonList(fødselsdato));
    }

    @JsonCreator
    public Fødsel(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("fødselsdato") List<LocalDate> fødselsdato) {
        super(antallBarn);
        this.fødselsdato = fødselsdato;
    }
}
