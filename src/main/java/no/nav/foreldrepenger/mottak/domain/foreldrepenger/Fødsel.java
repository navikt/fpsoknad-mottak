package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.BarnOgFødselsdatoer;
import no.nav.foreldrepenger.mottak.domain.validation.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
//@BarnOgFødselsdatoer TODO: Denne funker ikke med foreldrepenger.Fødsel, kun med felles.Fødsel. Må ryddes opp i.
public class Fødsel extends RelasjonTilBarnMedVedlegg {

    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.fødsel.fødselsdato}") LocalDate> fødselsdato;

    public Fødsel(LocalDate fødselsdato) {
        this(1, singletonList(fødselsdato), emptyList());
    }

    public Fødsel(int antallBarn, LocalDate fødselsDato) {
        this(antallBarn, singletonList(fødselsDato), emptyList());
    }

    @JsonCreator
    public Fødsel(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("fødselsdato") List<LocalDate> fødselsdato, List<String> vedlegg) {
        super(antallBarn, vedlegg);
        this.fødselsdato = fødselsdato;
    }
}
