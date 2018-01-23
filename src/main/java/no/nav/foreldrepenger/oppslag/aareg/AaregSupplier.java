package no.nav.foreldrepenger.oppslag.aareg;

import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;

import java.time.LocalDate;
import java.util.function.Supplier;

public class AaregSupplier implements Supplier<LookupResult<Arbeidsforhold>> {

    private final AaregClient aaregClient;
    private final Fodselsnummer fnr;
    private final int nrOfMonths;

    public AaregSupplier(AaregClient aaregClient, Fodselsnummer fnr, int nrOfMonths) {
        this.nrOfMonths = nrOfMonths;
        this.fnr = fnr;
        this.aaregClient = aaregClient;
    }

    @Override
    public LookupResult<Arbeidsforhold> get() {
        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(nrOfMonths);
        return new LookupResult<Arbeidsforhold>(
            "Aareg", LookupStatus.SUCCESS, aaregClient.arbeidsforhold(fnr, earlier, now));
    }
}
