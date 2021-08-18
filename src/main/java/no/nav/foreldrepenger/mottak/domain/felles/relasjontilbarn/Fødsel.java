package no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn;

import static java.util.Collections.emptyList;
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
public final class Fødsel extends RelasjonTilBarn {

    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.fødsel.fødselsdato}") LocalDate> fødselsdato;
    private final LocalDate termindato;

    public Fødsel(LocalDate fødselsdato) {
        this(fødselsdato, null);
    }

    public Fødsel(int antallBarn, LocalDate foedselsdato) {
        this(antallBarn, foedselsdato, null);
    }

    public Fødsel(LocalDate fødselsdato, LocalDate termindato) {
        this(1, fødselsdato, termindato);
    }

    public Fødsel(int antallBarn, LocalDate fødselsDato, LocalDate termindato) {
        this(antallBarn, singletonList(fødselsDato), termindato, emptyList());
    }

    @JsonCreator
    public Fødsel(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("fødselsdato") List<LocalDate> fødselsdato, LocalDate termindato, List<String> vedlegg) {
        super(antallBarn, vedlegg);
        this.fødselsdato = fødselsdato;
        this.termindato = termindato;
    }

    @Override
    public LocalDate relasjonsDato() {
        return fødselsdato.get(0);
    }
}
