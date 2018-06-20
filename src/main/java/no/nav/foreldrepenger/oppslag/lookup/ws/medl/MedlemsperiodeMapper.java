package no.nav.foreldrepenger.oppslag.lookup.ws.medl;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.time.DateUtil;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Medlemsperiode;

public class MedlemsperiodeMapper {

    private MedlemsperiodeMapper() {

    }

    public static MedlPeriode map(Medlemsperiode periode) {
        return new MedlPeriode(
                DateUtil.toLocalDate(periode.getFraOgMed()),
                Optional.ofNullable(periode.getTilOgMed()).map(DateUtil::toLocalDate),
                periode.getStatus().getTerm(),
                periode.getType().getTerm(),
                periode.getGrunnlagstype().getTerm(),
                periode.getLand().getTerm());
    }

}
