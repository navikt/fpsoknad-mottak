package no.nav.foreldrepenger.person;

import static org.junit.Assert.*;

import org.joda.time.*;
import org.joda.time.format.*;
import org.junit.jupiter.api.*;

import no.nav.foreldrepenger.domain.*;

public class TestChildSelector {

	private static final Fodselsnummer MOR = new Fodselsnummer("28016432662");

	@Test
	public void testNullFnr() {
		assertFalse(new FNRSjekkendeBarneVelger(6).isEligible(MOR, null));
	}

	@Test
	public void testLengeSidenFnr() {
		int months = 2;
		Barn barn = new Barn(MOR,
		        new Fodselsnummer(
		                DateTimeFormat.forPattern("ddMMyy").print(DateTime.now().minusMonths(months)) + "36325"),
		        DateTime.now().minusMonths(months).toLocalDate());
		assertTrue(new FNRSjekkendeBarneVelger(months + 1).isEligible(MOR, barn));
		assertFalse(new FNRSjekkendeBarneVelger(months - 1).isEligible(MOR, barn));
	}

	@Test
	public void testChildSelector() {
		int months = 2;
		Barn barn = new Barn(MOR,
		        new Fodselsnummer(
		                DateTimeFormat.forPattern("ddMMyy").print(DateTime.now().minusMonths(months)) + "36325"),
		        DateTime.now().minusMonths(months - 1).toLocalDate());
		assertTrue(new BarnMorRelasjonSjekkendeBarneVelger(months).isEligible(MOR, barn));
		barn = new Barn(MOR,
		        new Fodselsnummer(
		                DateTimeFormat.forPattern("ddMMyy").print(DateTime.now().minusMonths(months)) + "36325"),
		        DateTime.now().minusMonths(months + 1).toLocalDate());
		assertFalse(new BarnMorRelasjonSjekkendeBarneVelger(months + 1).isEligible(MOR, barn));
	}
}
