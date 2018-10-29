package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Rettighet;

@Data
@Rettighet
public class Rettigheter {

    private final boolean harAnnenForelderRett;
    private final boolean harOmsorgForBarnetIPeriodene;
    private final boolean harAleneOmsorgForBarnet;
    private final LocalDate datoForAleneomsorg;

}
