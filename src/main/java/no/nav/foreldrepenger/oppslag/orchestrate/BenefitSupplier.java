package no.nav.foreldrepenger.oppslag.orchestrate;

import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;

public abstract class BenefitSupplier implements Supplier<LookupResult<Benefit>> {

	private final Fodselsnummer fnr;
	private final int nrOfMonths;

	public BenefitSupplier(Fodselsnummer fnr, int nrOfMonths) {
		this.fnr = fnr;
		this.nrOfMonths = nrOfMonths;
	}

	protected Fodselsnummer getFnr() {
		return fnr;
	}

	protected int getNrOfMonths() {
		return nrOfMonths;
	}

}
