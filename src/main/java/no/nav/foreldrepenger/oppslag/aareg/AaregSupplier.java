package no.nav.foreldrepenger.oppslag.aareg;

import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;

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
