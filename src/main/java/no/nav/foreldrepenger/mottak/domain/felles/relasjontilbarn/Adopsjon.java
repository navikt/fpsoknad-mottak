package no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Adopsjon extends RelasjonTilBarn {

    @NotNull(message = "{ytelse.relasjontilbarn.adopsjon.omsorggsovertakelsesdato.notnull}")
    private final LocalDate omsorgsovertakelsesdato;
    private final boolean ektefellesBarn;
    private final boolean søkerAdopsjonAlene;

    private final LocalDate ankomstDato;
    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.adopsjon.fødselssdato.framtid}") LocalDate> fødselsdato;

    @JsonCreator
    public Adopsjon(@JsonProperty("antallBarn") int antallBarn,
            @JsonProperty("omsorgsovertakelsesdato") LocalDate omsorgsovertakelsesdato,
            @JsonProperty("ektefellesBarn") boolean ektefellesBarn,
            @JsonProperty("søkerAdopsjonAlene") boolean søkerAdopsjonAlene,
            @JsonProperty("vedlegg") List<String> vedlegg,
            @JsonProperty("ankomstDato") LocalDate ankomstDato,
            @JsonProperty("fødselsdato") List<LocalDate> fødselsdato) {
        super(antallBarn, vedlegg);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.ektefellesBarn = ektefellesBarn;
        this.ankomstDato = ankomstDato;
        this.fødselsdato = fødselsdato;
        this.søkerAdopsjonAlene = søkerAdopsjonAlene;
    }

    @Override
    public LocalDate relasjonsDato() {
        return fødselsdato.get(0);
    }
}
