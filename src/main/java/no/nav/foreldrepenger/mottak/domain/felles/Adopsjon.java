package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Adopsjon extends RelasjonTilBarn {

    @NotNull(message = "{ytelse.relasjontilbarn.adopsjon.omsorggsovertakelsesdato.notnull}")
    private final LocalDate omsorgsovertakelsesdato;
    private final boolean ektefellesBarn;
    private final LocalDate ankomstDato;
    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.adopsjon.fødselssdato.framtid}") LocalDate> fødselsdatoer;

    @JsonCreator
    public Adopsjon(LocalDate omsorgsovertakelsesdato,
            boolean ektefellesBarn,
            int antallBarn, LocalDate ankomstDato, LocalDate... fødselsdatoer) {
        super(antallBarn);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.ektefellesBarn = ektefellesBarn;
        this.ankomstDato = ankomstDato;
        this.fødselsdatoer = Arrays.asList(fødselsdatoer);
    }

    @Override
    public LocalDate relasjonsDato() {
        return omsorgsovertakelsesdato;
    }
}
