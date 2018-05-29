package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.validation.BarnOgFødselsdatoer;
import no.nav.foreldrepenger.mottak.domain.validation.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@BarnOgFødselsdatoer
public class Fødsel extends RelasjonTilBarnMedVedlegg {

    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.fødsel.fødselsdato}") LocalDate> fødselsdato;

    public Fødsel(LocalDate fødselsdato) {
        this(1, Collections.singletonList(fødselsdato), Collections.emptyList());
    }

    public Fødsel(int antallBarn, LocalDate fødselsDato) {
        this(antallBarn, Collections.singletonList(fødselsDato), Collections.emptyList());
    }

    @JsonCreator
    public Fødsel(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("fødselsdato") List<LocalDate> fødselsdato, List<Vedlegg> vedlegg) {
        super(antallBarn, vedlegg == null ? Collections.emptyList() : vedlegg);
        this.fødselsdato = fødselsdato;
    }
}
