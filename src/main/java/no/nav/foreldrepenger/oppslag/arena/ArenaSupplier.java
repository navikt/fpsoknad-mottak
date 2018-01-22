package no.nav.foreldrepenger.oppslag.arena;

import java.time.LocalDate;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;

public class ArenaSupplier implements Supplier<LookupResult<Ytelse>> {

    private final ArenaClient arenaClient;
    private final Fodselsnummer fnr;
    private final int nrOfMonths;

    public ArenaSupplier(ArenaClient arenaClient, Fodselsnummer fnr, int nrOfMonths) {
        this.nrOfMonths = nrOfMonths;
        this.fnr = fnr;
        this.arenaClient = arenaClient;
    }

    @Override
    public LookupResult<Ytelse> get() {
        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(nrOfMonths);
        return new LookupResult<Ytelse>("Arena", LookupStatus.SUCCESS, arenaClient.ytelser(fnr, earlier, now));
    }
}
