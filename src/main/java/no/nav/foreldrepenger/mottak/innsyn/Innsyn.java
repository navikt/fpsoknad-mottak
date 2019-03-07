package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Sak;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.Pingable;

public interface Innsyn extends Pingable {
    List<Sak> hentSaker(AktorId aktørId);

    List<Sak> hentSaker(String aktørId);

    List<UttaksPeriode> hentUttaksplan(String saksnummer);

    Vedtak hentVedtak(AktorId aktørId, String saksnummer);

}
