package no.nav.foreldrepenger.mottak.oppslag;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.http.Pingable;

public interface Oppslag extends Pingable {
    Person søker();

    AktørId aktørId();

    AktørId aktørId(Fødselsnummer fnr);

    Fødselsnummer fnr(AktørId aktørId);

    Navn navn(String id);

}
