package no.nav.foreldrepenger.oppslag.person;

import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;

public interface Barnutvelger {

	boolean erStonadsberettigetBarn(Fodselsnummer fnrMor, Barn barn);

}
