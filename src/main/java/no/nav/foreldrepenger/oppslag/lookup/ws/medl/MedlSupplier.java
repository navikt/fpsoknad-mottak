package no.nav.foreldrepenger.oppslag.lookup.ws.medl;

import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.lookup.LookupResult;
import no.nav.foreldrepenger.oppslag.lookup.LookupStatus;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;

public class MedlSupplier implements Supplier<LookupResult<MedlPeriode>> {

    private final MedlClient medlClient;
    private final Fodselsnummer fnr;

    public MedlSupplier(MedlClient medlClient, Fodselsnummer fnr) {
        this.medlClient = medlClient;
        this.fnr = fnr;
    }

    @Override
    public LookupResult<MedlPeriode> get() {
        List<MedlPeriode> medlData = medlClient.medlInfo(fnr);
        return new LookupResult<>("Medl", LookupStatus.SUCCESS, medlData);
    }
}
