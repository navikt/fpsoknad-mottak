package no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UttaksplanDTO {

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
    private final List<UttaksPeriodeDTO> uttaksPerioder;

    @JsonCreator
    public UttaksplanDTO(@JsonProperty("termindato") LocalDate termindato,
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
            @JsonProperty("annenForelderErInformert") Boolean annenForelderErInformert,
            @JsonProperty("uttaksPerioder") List<UttaksPeriodeDTO> uttaksPerioder) {
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
        this.uttaksPerioder = uttaksPerioder;
    }
}
