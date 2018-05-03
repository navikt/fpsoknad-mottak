package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.fpsak;

import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.lookup.LookupResult;
import no.nav.foreldrepenger.oppslag.lookup.LookupStatus;
import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.lookup.ws.aktor.AktorId;

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
