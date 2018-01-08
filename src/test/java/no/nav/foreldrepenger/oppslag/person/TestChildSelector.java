package no.nav.foreldrepenger.oppslag.person;

import static org.junit.Assert.*;

import org.joda.time.*;
import org.joda.time.format.*;
import org.junit.jupiter.api.*;

import no.nav.foreldrepenger.oppslag.domain.*;
import no.nav.foreldrepenger.oppslag.person.BarnMorRelasjonSjekkendeBarnutvelger;
import no.nav.foreldrepenger.oppslag.person.FNRSjekkendeBarnutvelger;

public class TestChildSelector {

	private static final Fodselsnummer MOR = new Fodselsnummer("28016432662");

	@Test
	public void testNullFnr() {
		assertFalse(new FNRSjekkendeBarnutvelger(6).erStonadsberettigetBarn(MOR, null));
	}

	@Test
	public void testLengeSidenFnr() {
		int months = 2;
		Barn barn = new Barn(MOR,
		        new Fodselsnummer(
		                DateTimeFormat.forPattern("ddMMyy").print(DateTime.now().minusMonths(months)) + "36325"),
		        DateTime.now().minusMonths(months).toLocalDate());
		assertTrue(new FNRSjekkendeBarnutvelger(months + 1).erStonadsberettigetBarn(MOR, barn));
		assertFalse(new FNRSjekkendeBarnutvelger(months - 1).erStonadsberettigetBarn(MOR, barn));
	}

	@Test
	public void testChildSelector() {
		int months = 2;
		Barn barn = new Barn(MOR,
		        new Fodselsnummer(
		                DateTimeFormat.forPattern("ddMMyy").print(DateTime.now().minusMonths(months)) + "36325"),
		        DateTime.now().minusMonths(months - 1).toLocalDate());
		assertTrue(new BarnMorRelasjonSjekkendeBarnutvelger(months).erStonadsberettigetBarn(MOR, barn));
		barn = new Barn(MOR,
		        new Fodselsnummer(
		                DateTimeFormat.forPattern("ddMMyy").print(DateTime.now().minusMonths(months)) + "36325"),
		        DateTime.now().minusMonths(months + 1).toLocalDate());
		assertFalse(new BarnMorRelasjonSjekkendeBarnutvelger(months + 1).erStonadsberettigetBarn(MOR, barn));
	}
}
