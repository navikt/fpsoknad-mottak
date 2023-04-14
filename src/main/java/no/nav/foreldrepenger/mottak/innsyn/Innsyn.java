package no.nav.foreldrepenger.mottak.innsyn;

import java.util.Optional;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.mottak.http.Pingable;

public interface Innsyn extends Pingable {

    Saker saker(Fødselsnummer fnr);

    Optional<AnnenPartVedtak> annenPartVedtak(AktørId aktørId, AnnenPartVedtakIdentifikator annenPartVedtakIdentifikator);
}
