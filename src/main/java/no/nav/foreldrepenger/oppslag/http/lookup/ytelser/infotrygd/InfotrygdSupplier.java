package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.http.lookup.LookupResult;
import no.nav.foreldrepenger.oppslag.http.lookup.LookupStatus;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;

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
