package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.innsending.Pingable;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;

public interface Innsyn extends Pingable {
    List<Sak> hentSaker(AktørId aktørId);

    List<Sak> hentSaker(String aktørId);

    Uttaksplan hentUttaksplan(String saksnummer);

    Vedtak hentVedtak(AktørId aktørId, String saksnummer);

    Uttaksplan hentUttaksplan(AktørId aktørId, AktørId annenPart);

}
