package org.foreldrepenger.selvbetjening.person.klient;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import no.nav.foreldrepenger.selvbetjening.domain.Barn;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;
import no.nav.foreldrepenger.selvbetjening.person.klient.FNRSjekkendeBarneVelger;
import no.nav.foreldrepenger.selvbetjening.person.klient.BarnMorRelasjonSjekkendeBarneVelger;

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
