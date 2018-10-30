package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public final class CounterRegistry {

    public static final Counter FP_SENDFEIL = counter("feil");
    public static final Counter FP_FØRSTEGANG = counter(INITIELL.name());
    public static final Counter FP_ENDRING = counter(ENDRING.name());
    public static final Counter FP_ETTERSSENDING = counter(ETTERSENDING.name());

    private CounterRegistry() {

    }

    private static Counter counter(String type) {
        return Metrics.counter("fpfordel.send", "ytelse", "foreldrepenger", "type", type);
    }
}
