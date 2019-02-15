package no.nav.foreldrepenger.mottak.domain.felles;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        this(1, omsorgsovertakelsesdato, årsak, singletonList(fødselsdato));
    }

    @JsonCreator
    public Omsorgsovertakelse(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("omsorgsovertakelsesdato") LocalDate omsorgsovertakelsesdato,
            @JsonProperty("årsak") OmsorgsOvertakelsesÅrsak årsak,
            @JsonProperty("fødsesdatoer") List<LocalDate> fødselsdatoer) {
        super(antallBarn);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.årsak = årsak;
        this.fødselsdatoer = Optional.ofNullable(fødselsdatoer).orElse(emptyList());
    }

    @Override
    public LocalDate relasjonsDato() {
        return omsorgsovertakelsesdato;
    }

}
