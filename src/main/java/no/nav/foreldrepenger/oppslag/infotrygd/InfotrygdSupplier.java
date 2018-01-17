package no.nav.foreldrepenger.oppslag.infotrygd;

import java.time.LocalDate;
import java.util.Objects;

import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.orchestrate.BenefitSupplier;

public class InfotrygdSupplier extends BenefitSupplier {

	private final InfotrygdClient infotrygdClient;

	public InfotrygdSupplier(InfotrygdClient infotrygdClient, Fodselsnummer fnr, int nrOfMonths) {
		super(fnr, nrOfMonths);
		this.infotrygdClient = Objects.requireNonNull(infotrygdClient);
	}

	@Override
	public LookupResult<Benefit> get() {
		LocalDate now = LocalDate.now();
		LocalDate earlier = now.minusMonths(getNrOfMonths());
		return new LookupResult<>("Infotrygd", LookupStatus.SUCCESS, infotrygdClient.casesFor(getFnr(), earlier, now));
	}
}
