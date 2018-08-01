package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Adopsjon extends RelasjonTilBarnMedVedlegg {

    @NotNull(message = "{ytelse.relasjontilbarn.adopsjon.omsorggsovertakelsesdato.notnull}")
    private final LocalDate omsorgsovertakelsesdato;
    private final boolean ektefellesBarn;
    private final LocalDate ankomstDato;
    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.adopsjon.fødselssdato.framtid}") LocalDate> fødselsdato;

    public Adopsjon(LocalDate omsorgsovertakelsesdato, boolean ektefellesBarn, LocalDate fødselsdato) {
        this(omsorgsovertakelsesdato, ektefellesBarn, 1, emptyList(), null, fødselsdato);
    }

    @JsonCreator
    public Adopsjon(LocalDate omsorgsovertakelsesdato,
            boolean ektefellesBarn,
            int antallBarn, List<String> vedlegg, LocalDate ankomstDato, LocalDate... fødselsdato) {
        super(antallBarn, vedlegg);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.ektefellesBarn = ektefellesBarn;
        this.ankomstDato = ankomstDato;
        this.fødselsdato = asList(fødselsdato);
    }
}
