package no.nav.foreldrepenger.oppslag.fpsak;

import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;

public class FpsakSupplier implements Supplier<LookupResult<Ytelse>> {

	private final FpsakClient fpsakClient;
	private final AktorId aktor;

	public FpsakSupplier(FpsakClient fpsakClient, AktorId aktor) {
		this.fpsakClient = fpsakClient;
		this.aktor = aktor;
	}

	@Override
	public LookupResult<Ytelse> get() {
		List<Ytelse> benefitData = fpsakClient.casesFor(aktor);
		return new LookupResult<>("Fpsak", LookupStatus.SUCCESS, benefitData);
	}
}
