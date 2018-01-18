package no.nav.foreldrepenger.oppslag.infotrygd;

import java.time.LocalDate;
import java.util.Objects;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.orchestrate.YtelseSupplier;

public class InfotrygdSupplier extends YtelseSupplier {

	private final InfotrygdClient infotrygdClient;

	public InfotrygdSupplier(InfotrygdClient infotrygdClient, Fodselsnummer fnr, int nrOfMonths) {
		super(fnr, nrOfMonths);
		this.infotrygdClient = Objects.requireNonNull(infotrygdClient);
	}

	@Override
	public LookupResult<Ytelse> get() {
		LocalDate now = LocalDate.now();
		LocalDate earlier = now.minusMonths(getNrOfMonths());
		return new LookupResult<>("Infotrygd", LookupStatus.SUCCESS, infotrygdClient.casesFor(getFnr(), earlier, now));
	}
}
