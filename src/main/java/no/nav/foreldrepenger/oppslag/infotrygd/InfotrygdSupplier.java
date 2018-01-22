package no.nav.foreldrepenger.oppslag.infotrygd;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;

public class InfotrygdSupplier implements Supplier<LookupResult<Ytelse>> {

    private final InfotrygdClient infotrygdClient;
    private final Fodselsnummer fnr;
    private final int nrOfMonths;

    public InfotrygdSupplier(InfotrygdClient infotrygdClient, Fodselsnummer fnr, int nrOfMonths) {
        this.fnr = fnr;
        this.nrOfMonths = nrOfMonths;
        this.infotrygdClient = Objects.requireNonNull(infotrygdClient);
    }

    @Override
    public LookupResult<Ytelse> get() {
        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(nrOfMonths);
        return new LookupResult<>("Infotrygd", LookupStatus.SUCCESS, infotrygdClient.casesFor(fnr, earlier, now));
    }
}
