package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.OmsorgsOvertakelsesÅrsak;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Omsorgsovertakelse extends RelasjonTilBarnMedVedlegg {

    private final LocalDate omsorgsovertakelsesdato;
    private final OmsorgsOvertakelsesÅrsak årsak;
    private final List<LocalDate> fødselsdato;
    private String beskrivelse;

    public Omsorgsovertakelse(LocalDate omsorgsovertakelsesdato, OmsorgsOvertakelsesÅrsak årsak, LocalDate fødselsdato) {
        this(1, omsorgsovertakelsesdato, årsak, singletonList(fødselsdato), emptyList());
    }

    @JsonCreator
    public Omsorgsovertakelse(int antallBarn,
                              LocalDate omsorgsovertakelsesdato,
                              OmsorgsOvertakelsesÅrsak årsak,
                              List<LocalDate> fødselsdato, List<String> vedlegg) {
        super(antallBarn, vedlegg);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.årsak = årsak;
        this.fødselsdato = fødselsdato;
    }
}
