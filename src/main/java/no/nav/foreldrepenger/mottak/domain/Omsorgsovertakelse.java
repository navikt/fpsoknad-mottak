package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Omsorgsovertakelse extends RelasjonTilBarn {

    private final LocalDate omsorgsovertakelsesdato;
    private final OmsorgsOvertakelsesÅrsak årsak;
    private LocalDate fødselsdato;
    private String beskrivelse;

    public Omsorgsovertakelse(LocalDate omsorgsovertakelsesdato, OmsorgsOvertakelsesÅrsak årsak) {
        this(1, omsorgsovertakelsesdato, årsak);
    }

    @ConstructorProperties({ "antallBarn", "omsorgsovertakelsesdato", "årsak" })
    public Omsorgsovertakelse(int antallBarn, LocalDate omsorgsovertakelsesdato, OmsorgsOvertakelsesÅrsak årsak) {
        super(antallBarn);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.årsak = årsak;
    }

}
