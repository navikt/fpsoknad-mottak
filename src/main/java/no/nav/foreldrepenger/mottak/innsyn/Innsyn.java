package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.innsending.Pingable;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.Uttaksplan;
import no.nav.foreldrepenger.mottak.innsyn.vedtak.Vedtak;

public interface Innsyn extends Pingable {
    List<Sak> hentSaker(AktorId aktørId);

    List<Sak> hentSaker(String aktørId);

    Uttaksplan hentUttaksplan(String saksnummer);

    Vedtak hentVedtak(AktorId aktørId, String saksnummer);

}
