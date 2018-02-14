package no.nav.foreldrepenger.oppslag.person;

import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Tag("fast")
public class TestChildSelector {

	private static final Fodselsnummer MOR = new Fodselsnummer("28016432662");

	private static final String CHILD_FNR = DateTimeFormatter.ofPattern("ddMMyy")
      .format(LocalDate.now().minusMonths(2)) + "36325";

	@Test
	public void testNullFnr() {
		assertFalse(new FNRSjekkendeBarnutvelger(6).erStonadsberettigetBarn(MOR, null));
	}

	@Test
	public void testLengeSidenFnr() {
		int months = 2;
		Barn barn = new Barn(MOR,
		        new Fodselsnummer(CHILD_FNR),
		        LocalDate.now().minusMonths(months));
		assertTrue(new FNRSjekkendeBarnutvelger(months + 1).erStonadsberettigetBarn(MOR, barn));
		assertFalse(new FNRSjekkendeBarnutvelger(months - 1).erStonadsberettigetBarn(MOR, barn));
	}

	@Test
	public void testChildSelector() {
		int months = 2;
		Barn barn = new Barn(MOR,
		        new Fodselsnummer(CHILD_FNR),
		        LocalDate.now().minusMonths(months - 1));
		assertTrue(new BarnMorRelasjonSjekkendeBarnutvelger(months).erStonadsberettigetBarn(MOR, barn));
		barn = new Barn(MOR,
		        new Fodselsnummer(CHILD_FNR),
		        LocalDate.now().minusMonths(months + 1));
		assertFalse(new BarnMorRelasjonSjekkendeBarnutvelger(months + 1).erStonadsberettigetBarn(MOR, barn));
	}
}
