package no.nav.foreldrepenger.oppslag.person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;

public class FNRSjekkendeBarnutvelger implements Barnutvelger {

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("ddMMyy");

	private final int monthsBack;

	public FNRSjekkendeBarnutvelger(int monthsBack) {
		this.monthsBack = monthsBack;
	}

	@Override
	public boolean erStonadsberettigetBarn(Fodselsnummer fnrMor, Barn barn) {
		return (barn != null) && (barn.getFnr().getFnr().length() == 11) && LocalDate
		        .parse(barn.getFnr().getFnr().substring(0, 6), FMT).isAfter(LocalDate.now().minusMonths(monthsBack));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
	}
}
