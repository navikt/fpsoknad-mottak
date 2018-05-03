package no.nav.foreldrepenger.oppslag.http.lookup.aareg;

import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.http.lookup.LookupResult;
import no.nav.foreldrepenger.oppslag.http.lookup.LookupStatus;
import no.nav.foreldrepenger.oppslag.http.lookup.person.Fodselsnummer;

public class AaregSupplier implements Supplier<LookupResult<Arbeidsforhold>> {

    private final AaregClient aaregClient;
    private final Fodselsnummer fnr;

    public AaregSupplier(AaregClient aaregClient, Fodselsnummer fnr) {
        this.fnr = fnr;
        this.aaregClient = aaregClient;
    }

    @Override
    public LookupResult<Arbeidsforhold> get() {
        return new LookupResult<Arbeidsforhold>(
                "Aareg", LookupStatus.SUCCESS, aaregClient.arbeidsforhold(fnr));
    }
}
