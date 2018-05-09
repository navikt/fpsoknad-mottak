package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Omsorgsovertakelse extends RelasjonTilBarnMedVedlegg {

    private final LocalDate omsorgsovertakelsesdato;
    private final OmsorgsOvertakelsesÅrsak årsak;
    private final List<LocalDate> fødselsdatoer;
    private String beskrivelse;

    public Omsorgsovertakelse(LocalDate omsorgsovertakelsesdato, OmsorgsOvertakelsesÅrsak årsak,
            LocalDate fødselsdato) {
        this(1, omsorgsovertakelsesdato, årsak, Collections.singletonList(fødselsdato), Collections.emptyList());
    }

    @JsonCreator
    public Omsorgsovertakelse(int antallBarn,
            LocalDate omsorgsovertakelsesdato,
            OmsorgsOvertakelsesÅrsak årsak,
            List<LocalDate> fødselsdatoer, List<Vedlegg> vedlegg) {
        super(antallBarn, vedlegg == null ? Collections.emptyList() : vedlegg);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.årsak = årsak;
        this.fødselsdatoer = fødselsdatoer;
    }
}
