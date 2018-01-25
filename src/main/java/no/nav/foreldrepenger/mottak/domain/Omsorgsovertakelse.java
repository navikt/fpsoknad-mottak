package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Omsorgsovertakelse extends RelasjonTilBarn {

    private final LocalDate omsorgsovertakelsesdato;
    private final OmsorgsOvertakelsesÅrsak årsak;
    private LocalDate fødselsdato;
    private String beskrivelse;

    public Omsorgsovertakelse(LocalDate omsorgsovertakelsesdato, OmsorgsOvertakelsesÅrsak årsak) {
        this(1, omsorgsovertakelsesdato, årsak);
    }

    @JsonCreator
    public Omsorgsovertakelse(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("omsorgsovertakelsesdato") LocalDate omsorgsovertakelsesdato,
            @JsonProperty("årsak") OmsorgsOvertakelsesÅrsak årsak) {
        super(antallBarn);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.årsak = årsak;
    }

}
