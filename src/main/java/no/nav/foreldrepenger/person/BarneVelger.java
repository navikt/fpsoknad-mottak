package no.nav.foreldrepenger.person;

import no.nav.foreldrepenger.domain.Barn;
import no.nav.foreldrepenger.domain.Fodselsnummer;

public interface BarneVelger {

	boolean isEligible(Fodselsnummer fnrMor, Barn barn);

}
