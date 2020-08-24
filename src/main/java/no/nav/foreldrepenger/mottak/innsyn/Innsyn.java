package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.http.Pingable;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;

public interface Innsyn extends Pingable {
    List<Sak> saker(AktørId aktørId);

    Uttaksplan uttaksplan(String saksnummer);

    Vedtak vedtak(AktørId aktørId, String saksnummer);

    Uttaksplan uttaksplan(AktørId aktørId, AktørId annenPart);

}
