package no.nav.foreldrepenger.oppslag.orchestrate;

import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;

public abstract class YtelseSupplier implements Supplier<LookupResult<Ytelse>> {

	private final Fodselsnummer fnr;
	private final int nrOfMonths;

	public YtelseSupplier(Fodselsnummer fnr, int nrOfMonths) {
		this.fnr = fnr;
		this.nrOfMonths = nrOfMonths;
	}

	protected Fodselsnummer getFnr() {
		return fnr;
	}

	protected int getNrOfMonths() {
		return nrOfMonths;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [fnr=" + fnr + ", nrOfMonths=" + nrOfMonths + "]";
	}

}
