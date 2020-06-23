package no.nav.foreldrepenger.mottak.oppslag;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.http.RetryAware;
import no.nav.foreldrepenger.mottak.innsending.Pingable;

public interface Oppslag extends Pingable, RetryAware {
    Person getSøker();

    AktørId getAktørId();

    AktørId getAktørId(Fødselsnummer fnr);

    Fødselsnummer getFnr(AktørId aktørId);

    String getAktørIdAsString();

    Navn hentNavn(String fnr);
}
