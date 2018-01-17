package no.nav.foreldrepenger.oppslag.fpsak;

import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;

public class FpsakSupplier implements Supplier<LookupResult<Benefit>> {

	private final FpsakKlient fpsakClient;
	private final AktorId aktor;

	public FpsakSupplier(FpsakKlient fpsakClient, AktorId aktor) {
		this.fpsakClient = fpsakClient;
		this.aktor = aktor;
	}

	@Override
	public LookupResult<Benefit> get() {
		List<Benefit> benefitData = fpsakClient.casesFor(aktor);
		return new LookupResult<>("Fpsak", LookupStatus.SUCCESS, benefitData);
	}
}
