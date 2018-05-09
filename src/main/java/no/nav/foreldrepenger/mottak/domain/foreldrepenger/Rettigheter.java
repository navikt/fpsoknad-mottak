package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import lombok.Data;

@Data
public class Rettigheter {

    private final boolean harAnnenForelderRett;
    private final boolean harOmsorgForBarnetIPeriodene;
    private final boolean harAleneOmsorgForBarnet;

}
