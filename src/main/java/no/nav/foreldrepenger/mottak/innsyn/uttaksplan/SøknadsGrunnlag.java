package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SøknadsGrunnlag {
    private final FamiliehendelseType familieHendelseType;
    private final LocalDate familieHendelseDato;

    private final Integer dekningsgrad;
    private final Integer antallBarn;

    private final Boolean søkerErFarEllerMedmor;
    private final Boolean morErAleneOmOmsorg;
    private final Boolean morHarRett;
    private final Boolean morErUfør;

    private final Boolean farMedmorErAleneOmOmsorg;
    private final Boolean farMedmorHarRett;

    @JsonCreator
    public SøknadsGrunnlag(@JsonProperty("familieHendelseType") FamiliehendelseType familieHendelseType,
            @JsonProperty("familieHendelseDato") LocalDate familieHendelseDato,
            @JsonProperty("dekningsgrad") Integer dekningsgrad,
            @JsonProperty("antallBarn") Integer antallBarn,
            @JsonProperty("søkerErFarEllerMedmor") Boolean søkerErFarEllerMedmor,
            @JsonProperty("morErAleneOmOmsorg") Boolean morErAleneOmOmsorg,
            @JsonProperty("morHarRett") Boolean morHarRett,
            @JsonProperty("morErUfør") Boolean morErUfør,
            @JsonProperty("farMedmorErAleneOmOmsorg") Boolean farMedmorErAleneOmOmsorg,
            @JsonProperty("farMedmorHarRett") Boolean farMedmorHarRett) {
        this.familieHendelseType = familieHendelseType;
        this.familieHendelseDato = familieHendelseDato;
        this.dekningsgrad = dekningsgrad;
        this.antallBarn = antallBarn;
        this.søkerErFarEllerMedmor = søkerErFarEllerMedmor;
        this.morErAleneOmOmsorg = morErAleneOmOmsorg;
        this.morHarRett = morHarRett;
        this.morErUfør = morErUfør;
        this.farMedmorErAleneOmOmsorg = farMedmorErAleneOmOmsorg;
        this.farMedmorHarRett = farMedmorHarRett;
    }

}
