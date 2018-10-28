package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public final class CounterRegistry {

    private static final String YTELSE = "ytelse";
    private static final String TYPE = "type";
    private static final String FP = "foreldrepenger";
    private static final String NAVN = "fpfordel.send";

    public static final Counter FP_SENDFEIL = Metrics.counter(NAVN, YTELSE, FP, TYPE, "feil");
    public static final Counter FP_FØRSTEGANG = Metrics.counter(NAVN, YTELSE, FP, TYPE, INITIELL.name());
    public static final Counter FP_ENDRING = Metrics.counter(NAVN, YTELSE, FP, TYPE, ENDRING.name());
    public static final Counter FP_ETTERSSENDING = Metrics.counter(NAVN, YTELSE, FP, TYPE, ETTERSENDING.name());

    private CounterRegistry() {

    }

}
