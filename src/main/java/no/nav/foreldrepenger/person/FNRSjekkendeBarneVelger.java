package no.nav.foreldrepenger.person;

import org.joda.time.DateTime;
import org.joda.time.format.*;

import no.nav.foreldrepenger.domain.Barn;
import no.nav.foreldrepenger.domain.Fodselsnummer;

public class FNRSjekkendeBarneVelger implements BarneVelger {

	private static final DateTimeFormatter FMT = DateTimeFormat.forPattern("ddMMyy");

	private final int monthsBack;

	public FNRSjekkendeBarneVelger(int monthsBack) {
		this.monthsBack = monthsBack;
	}

	@Override
	public boolean isEligible(Fodselsnummer fnrMor, Barn barn) {
		return (barn != null) && (barn.getFnr().getFnr().length() == 11) && FMT
		        .parseDateTime(barn.getFnr().getFnr().substring(0, 6)).isAfter(DateTime.now().minusMonths(monthsBack));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
	}
}
