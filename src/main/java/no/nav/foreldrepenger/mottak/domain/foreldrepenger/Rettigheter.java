package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Rettighet;

@Data
@Rettighet
@ToString(exclude = "datoForAleneomsorg")
@EqualsAndHashCode(exclude = "datoForAleneomsorg")
public class Rettigheter {

    private final boolean harAnnenForelderRett;
    private final boolean harOmsorgForBarnetIPeriodene;
    private final boolean harAleneOmsorgForBarnet;
    private final LocalDate datoForAleneomsorg;

}
