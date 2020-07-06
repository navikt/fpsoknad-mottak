package no.nav.foreldrepenger.mottak.oppslag;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.http.Pingable;
import no.nav.foreldrepenger.mottak.http.RetryAware;

public interface Oppslag extends Pingable, RetryAware {
    Person søker();

    AktørId aktørId();

    AktørId aktørId(Fødselsnummer fnr);

    Fødselsnummer fnr(AktørId aktørId);

    Navn navn(Fødselsnummer fnr);
}
