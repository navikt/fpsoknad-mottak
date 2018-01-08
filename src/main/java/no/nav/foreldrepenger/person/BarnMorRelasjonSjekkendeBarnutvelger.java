package no.nav.foreldrepenger.person;

import org.joda.time.DateTime;

import no.nav.foreldrepenger.domain.Barn;
import no.nav.foreldrepenger.domain.Fodselsnummer;

public class BarnMorRelasjonSjekkendeBarnutvelger implements Barnutvelger {

	private final int monthsBack;

	public BarnMorRelasjonSjekkendeBarnutvelger(int months) {
		this.monthsBack = months;
	}

	@Override
	public boolean erStonadsberettigetBarn(Fodselsnummer fnrMor, Barn barn) {
		return fnrMor.equals(barn.getFnrMor())
		        && barn.getBirthDate().isAfter(DateTime.now().minusMonths(monthsBack).toLocalDate());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
	}
}
