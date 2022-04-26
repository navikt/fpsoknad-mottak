package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Sak;
import no.nav.foreldrepenger.common.innsyn.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.mottak.http.Pingable;

public interface Innsyn extends Pingable {
    List<Sak> saker(AktørId aktørId);

    UttaksplanDto uttaksplan(String saksnummer);

    UttaksplanDto uttaksplan(AktørId aktørId, AktørId annenPart);

    Saker sakerV2(AktørId aktørId);
}
