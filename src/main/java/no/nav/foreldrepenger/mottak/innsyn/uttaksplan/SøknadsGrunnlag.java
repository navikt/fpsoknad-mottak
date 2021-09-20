package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad;

@Data
public class SøknadsGrunnlag {
    private final LocalDate termindato;
    private final LocalDate fødselsdato;
    private final LocalDate omsorgsovertakelsesdato;

    private final Dekningsgrad dekningsgrad;
    private final Integer antallBarn;

    private final Boolean søkerErFarEllerMedmor;
    private final Boolean morErAleneOmOmsorg;
    private final Boolean morHarRett;
    private final Boolean morErUfør;

    private final Boolean farMedmorErAleneOmOmsorg;
    private final Boolean farMedmorHarRett;
    private final Boolean annenForelderErInformert;

    @JsonCreator
    public SøknadsGrunnlag(@JsonProperty("termindato") LocalDate termindato,
            @JsonProperty("fødselsdato") LocalDate fødselsdato,
            @JsonProperty("omsorgsovertakelsesdato") LocalDate omsorgsovertakelsesdato,
            @JsonProperty("dekningsgrad") Dekningsgrad dekningsgrad,
            @JsonProperty("antallBarn") Integer antallBarn,
            @JsonProperty("søkerErFarEllerMedmor") Boolean søkerErFarEllerMedmor,
            @JsonProperty("morErAleneOmOmsorg") Boolean morErAleneOmOmsorg,
            @JsonProperty("morHarRett") Boolean morHarRett,
            @JsonProperty("morErUfør") Boolean morErUfør,
            @JsonProperty("farMedmorErAleneOmOmsorg") Boolean farMedmorErAleneOmOmsorg,
            @JsonProperty("farMedmorHarRett") Boolean farMedmorHarRett,
            @JsonProperty("annenForelderErInformert") Boolean annenForelderErInformert) {
        this.omsorgsovertakelsesdato = omsorgsovertakelsesdato;
        this.fødselsdato = fødselsdato;
        this.termindato = termindato;
        this.dekningsgrad = dekningsgrad;
        this.antallBarn = antallBarn;
        this.søkerErFarEllerMedmor = søkerErFarEllerMedmor;
        this.morErAleneOmOmsorg = morErAleneOmOmsorg;
        this.morHarRett = morHarRett;
        this.morErUfør = morErUfør;
        this.farMedmorErAleneOmOmsorg = farMedmorErAleneOmOmsorg;
        this.farMedmorHarRett = farMedmorHarRett;
        this.annenForelderErInformert = annenForelderErInformert;
    }
}
