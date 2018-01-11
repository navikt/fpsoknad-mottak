package no.nav.foreldrepenger.oppslag.person;

import java.time.LocalDate;

import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;

public class BarnMorRelasjonSjekkendeBarnutvelger implements Barnutvelger {

	private final int monthsBack;

	public BarnMorRelasjonSjekkendeBarnutvelger(int months) {
		this.monthsBack = months;
	}

	@Override
	public boolean erStonadsberettigetBarn(Fodselsnummer fnrMor, Barn barn) {
		return fnrMor.equals(barn.getFnrMor())
		        && barn.getBirthDate().isAfter(LocalDate.now().minusMonths(monthsBack));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
	}
}
