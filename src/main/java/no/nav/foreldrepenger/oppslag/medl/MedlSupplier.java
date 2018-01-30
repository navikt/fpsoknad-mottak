package no.nav.foreldrepenger.oppslag.medl;

import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;

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
