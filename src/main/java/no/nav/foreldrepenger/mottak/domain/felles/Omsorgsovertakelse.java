package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
    private final List<LocalDate> fødselsdatoer;
    private String beskrivelse;

    public Omsorgsovertakelse(LocalDate omsorgsovertakelsesdato, OmsorgsOvertakelsesÅrsak årsak,
            LocalDate fødselsdato) {
        this(1, omsorgsovertakelsesdato, årsak, Collections.singletonList(fødselsdato));
    }

    @JsonCreator
    public Omsorgsovertakelse(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("omsorgsovertakelsesdato") LocalDate omsorgsovertakelsesdato,
            @JsonProperty("årsak") OmsorgsOvertakelsesÅrsak årsak,
            @JsonProperty("fødsesdatoer") List<LocalDate> fødselsdatoer) {
        super(antallBarn);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.årsak = årsak;
        this.fødselsdatoer = fødselsdatoer;
    }

}
