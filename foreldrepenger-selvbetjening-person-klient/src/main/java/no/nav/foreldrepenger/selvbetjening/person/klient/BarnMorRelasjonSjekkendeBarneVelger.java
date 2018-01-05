package no.nav.foreldrepenger.selvbetjening.person.klient;

import org.joda.time.DateTime;

import no.nav.foreldrepenger.selvbetjening.domain.Barn;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;

public class BarnMorRelasjonSjekkendeBarneVelger implements BarneVelger {


	private final int monthsBack;

	public BarnMorRelasjonSjekkendeBarneVelger(int months) {
		this.monthsBack = months;
	}

	@Override
	public boolean isEligible(Fodselsnummer fnrMor, Barn barn) {
		return fnrMor.equals(barn.getFnrMor())
		        && barn.getBirthDate().isAfter(DateTime.now().minusMonths(monthsBack).toLocalDate());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
	}
}
