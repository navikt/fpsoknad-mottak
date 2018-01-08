package no.nav.foreldrepenger.person;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import no.nav.foreldrepenger.domain.Barn;
import no.nav.foreldrepenger.domain.Fodselsnummer;

public class FNRSjekkendeBarnutvelger implements Barnutvelger {

	private static final DateTimeFormatter FMT = DateTimeFormat.forPattern("ddMMyy");

	private final int monthsBack;

	public FNRSjekkendeBarnutvelger(int monthsBack) {
		this.monthsBack = monthsBack;
	}

	@Override
	public boolean erStonadsberettigetBarn(Fodselsnummer fnrMor, Barn barn) {
		return (barn != null) && (barn.getFnr().getFnr().length() == 11) && FMT
		        .parseDateTime(barn.getFnr().getFnr().substring(0, 6)).isAfter(DateTime.now().minusMonths(monthsBack));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
	}
}
