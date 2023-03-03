package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.mottak.http.Pingable;

import java.util.Optional;

public interface Innsyn extends Pingable {

    Saker saker(AktørId aktørId);

    Optional<AnnenPartVedtak> annenPartVedtak(AktørId aktørId, AnnenPartVedtakIdentifikator annenPartVedtakIdentifikator);
}
