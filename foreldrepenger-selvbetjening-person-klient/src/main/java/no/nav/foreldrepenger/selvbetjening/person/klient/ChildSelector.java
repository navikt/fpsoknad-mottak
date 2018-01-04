package no.nav.foreldrepenger.selvbetjening.person.klient;

import no.nav.foreldrepenger.selvbetjening.domain.Barn;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;

public interface ChildSelector {
	
	boolean isEligible(Fodselsnummer fnrMor,Barn barn);

}
