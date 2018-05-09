package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.validation.PastOrToday;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Adopsjon extends RelasjonTilBarnMedVedlegg {

    @NotNull(message = "{ytelse.relasjontilbarn.adopsjon.omsorggsovertakelsesdato.notnull}")
    private final LocalDate omsorgsovertakelsesdato;
    private final boolean ektefellesBarn;
    private final LocalDate ankomstDato;
    private final List<@PastOrToday(message = "{ytelse.relasjontilbarn.adopsjon.fødselssdato.framtid}") LocalDate> fødselsdatoer;

    public Adopsjon(LocalDate omsorgsovertakelsesdato, boolean ektefellesBarn, LocalDate fødselsdato) {
        this(omsorgsovertakelsesdato, ektefellesBarn, 1, Collections.emptyList(), null, fødselsdato);
    }

    @JsonCreator
    public Adopsjon(LocalDate omsorgsovertakelsesdato,
            boolean ektefellesBarn,
            int antallBarn, List<Vedlegg> vedlegg, LocalDate ankomstDato, LocalDate... fødselsdatoer) {
        super(antallBarn, vedlegg == null ? Collections.emptyList() : vedlegg);
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.ektefellesBarn = ektefellesBarn;
        this.ankomstDato = ankomstDato;
        this.fødselsdatoer = Arrays.asList(fødselsdatoer);
    }
}
