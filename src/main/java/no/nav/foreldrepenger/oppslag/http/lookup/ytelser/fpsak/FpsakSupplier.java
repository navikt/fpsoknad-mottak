package no.nav.foreldrepenger.oppslag.http.lookup.ytelser.fpsak;

import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.http.lookup.LookupResult;
import no.nav.foreldrepenger.oppslag.http.lookup.LookupStatus;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.http.lookup.aktor.AktorId;

public class FpsakSupplier implements Supplier<LookupResult<Ytelse>> {

    private final FpsakClient fpsakClient;
    private final AktorId aktor;

    public FpsakSupplier(FpsakClient fpsakClient, AktorId aktor) {
        this.fpsakClient = fpsakClient;
        this.aktor = aktor;
    }

    @Override
    public LookupResult<Ytelse> get() {
        return new LookupResult<>("Fpsak", LookupStatus.SUCCESS, fpsakClient.casesFor(aktor));
    }
}
