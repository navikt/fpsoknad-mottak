package no.nav.foreldrepenger.mottak.oppslag;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.Pingable;

public interface Oppslag extends Pingable {

    Person getSøker();

    AktorId getAktørId();

    AktorId getAktørId(Fødselsnummer fnr);

    Fødselsnummer getFnr(AktorId aktørId);

    List<Arbeidsforhold> getArbeidsforhold();

}
