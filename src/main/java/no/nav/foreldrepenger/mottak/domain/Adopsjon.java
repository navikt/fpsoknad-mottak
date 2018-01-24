package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Adopsjon extends RelasjonTilBarn {

    private final LocalDate omsorgsovertakelsesdato;
    private final boolean ektefellesBarn;
    private LocalDate ankomstDato;
    private LocalDate fødselsdato;
    
 public Adopsjon(LocalDate omsorgsovertakelsesdato, boolean ektefellesBarn) {
        this(omsorgsovertakelsesdato,ektefellesBarn,1);
    }

    @Builder
    @ConstructorProperties({ "omsorgsovertakelsesdato", "ektefellesBarn", "antallBarn" })
    public Adopsjon(LocalDate omsorgsovertakelsesdato, boolean ektefellesBarn, int antallBarn) {
        super(antallBarn);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.ektefellesBarn = ektefellesBarn;

    }

}
