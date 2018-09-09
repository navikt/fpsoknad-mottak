package no.nav.foreldrepenger.lookup.ws.aktor;

import no.nav.foreldrepenger.lookup.Pingable;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

public interface AktorIdClient extends Pingable {

    AktorId aktorIdForFnr(Fødselsnummer fnr);

    Fødselsnummer fnrForAktørId(AktorId fnr);

}
