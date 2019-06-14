package no.nav.foreldrepenger.mottak.oppslag;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.innsending.Pingable;

public interface Oppslag extends Pingable {
    Person getSøker();

    AktørId getAktørId();

    AktørId getAktørId(Fødselsnummer fnr);

    Fødselsnummer getFnr(AktørId aktørId);

    List<Arbeidsforhold> getArbeidsforhold();

    String getAktørIdAsString();
}
