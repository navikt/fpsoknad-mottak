package no.nav.foreldrepenger.mottak.innsyn;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Sak;

public interface Innsyn {
    List<Sak> hentSaker(AktorId aktørId);

    List<Sak> hentSaker(String aktørId);

    List<UttaksPeriode> hentUttaksplan(String saksnummer);

}
