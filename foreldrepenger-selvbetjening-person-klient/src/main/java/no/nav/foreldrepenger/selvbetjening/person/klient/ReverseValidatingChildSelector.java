package no.nav.foreldrepenger.selvbetjening.person.klient;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.selvbetjening.domain.Barn;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;

public class ReverseValidatingChildSelector implements ChildSelector {

	private static final Logger LOG = LoggerFactory.getLogger(ReverseValidatingChildSelector.class);

	private final int monthsBack;

	public ReverseValidatingChildSelector(int months) {
		this.monthsBack = months;
	}

	@Override
	public boolean isEligible(Fodselsnummer fnrMor, Barn barn) {
		return fnrMor.equals(barn.getFnrMor()) && barn.getBirthDate().isAfter(DateTime.now().minusMonths(monthsBack).toLocalDate());
	}
}
