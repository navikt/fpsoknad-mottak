package no.nav.foreldrepenger.person;

import no.nav.foreldrepenger.domain.Barn;
import no.nav.foreldrepenger.domain.Fodselsnummer;

public interface Barnutvelger {

	boolean erStonadsberettigetBarn(Fodselsnummer fnrMor, Barn barn);

}
