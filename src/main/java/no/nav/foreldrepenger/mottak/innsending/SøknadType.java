package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.util.CounterRegistry.*;

import io.micrometer.core.instrument.Counter;

public enum SøknadType {
    INITIELL_FORELDREPENGER(FP_FØRSTEGANG),
    ETTERSENDING_FORELDREPENGER(FP_ETTERSSENDING),
    ENDRING_FORELDREPENGER(FP_ENDRING),
    INITIELL_ENGANGSSTØNAD(ES_FØRSTEGANG),
    ETTERSENDING_ENGANGSSTØNAD(ES_ETTERSSENDING),
    INITIELL_ENGANGSSTØNAD_DOKMOT,
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

    public void count() {
        if (counter != null) {
            counter.increment();
        }
    }
}
