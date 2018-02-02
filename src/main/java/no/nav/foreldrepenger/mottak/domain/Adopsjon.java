package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Adopsjon extends RelasjonTilBarn {

    @NotNull(message = "ytelser.relasjontilbarn.adopsjon.omsorggsovertakelsesdato.notnull")
    private final LocalDate omsorgsovertakelsesdato;
    private final boolean ektefellesBarn;
    private LocalDate ankomstDato;
    @Past(message = "ytelser.relasjontilbarn.adopsjon.fødselssdato.framtid")
    private LocalDate fødselsdato;

    public Adopsjon(LocalDate omsorgsovertakelsesdato, boolean ektefellesBarn) {
        this(omsorgsovertakelsesdato, ektefellesBarn, 1);
    }

    @JsonCreator
    public Adopsjon(@JsonProperty("omsorgsovertakelsesdato") LocalDate omsorgsovertakelsesdato,
            @JsonProperty("ektefellesBarn") boolean ektefellesBarn,
            @JsonProperty("antallBarn") int antallBarn) {
        super(antallBarn);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.ektefellesBarn = ektefellesBarn;

    }

}
