package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.util.CounterRegistry.ES_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.ES_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_ENDRING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.SVP_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.SVP_FØRSTEGANG;

import io.micrometer.core.instrument.Counter;

public enum SøknadType {
    INITIELL_FORELDREPENGER(FP_FØRSTEGANG),
    ETTERSENDING_FORELDREPENGER(FP_ETTERSSENDING),
    ENDRING_FORELDREPENGER(FP_ENDRING),
    INITIELL_ENGANGSSTØNAD(ES_FØRSTEGANG),
    ETTERSENDING_ENGANGSSTØNAD(ES_ETTERSSENDING),
    INITIELL_ENGANGSSTØNAD_DOKMOT,
    INITIELL_SVANGERSKAPSPENGER(SVP_FØRSTEGANG),
    ETTERSENDING_SVANGERSKAPSPENGER(SVP_ETTERSSENDING),
    UKJENT;

    private final Counter counter;

    SøknadType() {
        this(null);
    }

    SøknadType(Counter counter) {
        this.counter = counter;
    }

    public boolean erForeldrepenger() {
        return this.equals(INITIELL_FORELDREPENGER)
                || this.equals(ENDRING_FORELDREPENGER)
                || this.equals(ETTERSENDING_FORELDREPENGER);
    }

    public boolean erEngangsstønad() {
        return this.equals(INITIELL_ENGANGSSTØNAD)
                || this.equals(ETTERSENDING_ENGANGSSTØNAD)
                || this.equals(INITIELL_ENGANGSSTØNAD_DOKMOT);
    }

    public boolean erSvangerskapspenger() {
        return this.equals(INITIELL_SVANGERSKAPSPENGER)
                || this.equals(ETTERSENDING_SVANGERSKAPSPENGER);
    }

    public void count() {
        if (counter != null) {
            counter.increment();
        }
    }
}
