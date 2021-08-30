package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.domain.FagsakType.ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.domain.FagsakType.FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.domain.FagsakType.SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.ES_ETTERSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.ES_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_ENDRING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_ETTERSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.SVP_ETTERSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.SVP_FØRSTEGANG;

import io.micrometer.core.instrument.Counter;
import no.nav.foreldrepenger.mottak.domain.FagsakType;

public enum SøknadType {
    INITIELL_FORELDREPENGER(FP_FØRSTEGANG),
    ETTERSENDING_FORELDREPENGER(FP_ETTERSENDING),
    ENDRING_FORELDREPENGER(FP_ENDRING),
    INITIELL_ENGANGSSTØNAD(ES_FØRSTEGANG),
    ETTERSENDING_ENGANGSSTØNAD(ES_ETTERSENDING),
    INITIELL_ENGANGSSTØNAD_DOKMOT,
    INITIELL_SVANGERSKAPSPENGER(SVP_FØRSTEGANG),
    ETTERSENDING_SVANGERSKAPSPENGER(SVP_ETTERSENDING),
    UKJENT;

    private final Counter counter;

    SøknadType() {
        this(null);
    }

    SøknadType(Counter counter) {
        this.counter = counter;
    }

    private boolean erForeldrepenger() {
        return this.equals(INITIELL_FORELDREPENGER)
                || this.equals(ENDRING_FORELDREPENGER)
                || this.equals(ETTERSENDING_FORELDREPENGER);
    }

    private boolean erEngangsstønad() {
        return this.equals(INITIELL_ENGANGSSTØNAD)
                || this.equals(ETTERSENDING_ENGANGSSTØNAD)
                || this.equals(INITIELL_ENGANGSSTØNAD_DOKMOT);
    }

    private boolean erSvangerskapspenger() {
        return this.equals(INITIELL_SVANGERSKAPSPENGER)
                || this.equals(ETTERSENDING_SVANGERSKAPSPENGER);
    }

    public boolean erUkjent() {
        return UKJENT.equals(this);
    }

    public FagsakType fagsakType() {
        
        
        if (erForeldrepenger()) {
            return FORELDREPENGER;
        }
        if (erEngangsstønad()) {
            return ENGANGSSTØNAD;
        }
        if (erSvangerskapspenger()) {
            return SVANGERSKAPSPENGER;
        }
        return FagsakType.UKJENT;

    }

    public void count() {
        if (counter != null) {
            counter.increment();
        }
    }

    public boolean erEttersending() {
        return this.equals(ETTERSENDING_SVANGERSKAPSPENGER) ||
                this.equals(ETTERSENDING_ENGANGSSTØNAD) ||
                this.equals(ETTERSENDING_FORELDREPENGER);
    }

    public boolean erEndring() {
        return this.equals(ENDRING_FORELDREPENGER);
    }

    public boolean erInitiellForeldrepenger() {
        return this.equals(INITIELL_FORELDREPENGER);
    }
}
