package no.nav.foreldrepenger.oppslag.inntekt;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;

public class InntektSupplier implements Supplier<LookupResult<Inntekt>> {

	private final InntektClient inntektClient;
	private final Fodselsnummer fnr;
	private final int nrOfMonths;

	public InntektSupplier(InntektClient inntektClient, Fodselsnummer fnr, int nrOfMonths) {
		this.inntektClient = inntektClient;
		this.fnr = fnr;
		this.nrOfMonths = nrOfMonths;
	}

	@Override
	public LookupResult<Inntekt> get() {
		LocalDate now = LocalDate.now();
		LocalDate earlier = now.minusMonths(nrOfMonths);
		List<Inntekt> incomeData = inntektClient.incomeForPeriod(fnr, earlier, now);
		return new LookupResult<>("Inntektskomponenten", LookupStatus.SUCCESS, incomeData);
	}
}
