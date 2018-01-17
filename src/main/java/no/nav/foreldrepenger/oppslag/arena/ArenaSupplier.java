package no.nav.foreldrepenger.oppslag.arena;

import java.time.LocalDate;

import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.orchestrate.BenefitSupplier;

public class ArenaSupplier extends BenefitSupplier {

	private final ArenaClient arenaClient;

	public ArenaSupplier(ArenaClient arenaClient, Fodselsnummer fnr, int nrOfMonths) {
		super(fnr, nrOfMonths);
		this.arenaClient = arenaClient;
	}

	@Override
	public LookupResult<Benefit> get() {
		LocalDate now = LocalDate.now();
		LocalDate earlier = now.minusMonths(getNrOfMonths());
		return new LookupResult<>("Arena", LookupStatus.SUCCESS, arenaClient.ytelser(getFnr(), earlier, now));
	}
}
