package no.nav.foreldrepenger.mottak.http;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public final class Counters {

    private static final String FØRSTEGANG = "initiell";
    private static final String ENDRING = "endring";
    private static final String ETTERSENDING = "ettersending";

    public static final Counter TELLER_SENDFEIL = Metrics.counter("fpfordel.send", "ytelse",
            "foreldrepenger", "type", "feil");

    public static final Counter TELLER_FØRSTEGANG = Metrics.counter("fpfordel.send", "ytelse", "foreldrepenger", "type",
            FØRSTEGANG);
    public static final Counter TELLER_ENDRING = Metrics.counter("fpfordel.send", "ytelse", "foreldrepenger", "type",
            ENDRING);
    public static final Counter TELLER_ETTERSSENDING = Metrics.counter("fpfordel.send", "ytelse", "foreldrepenger",
            "type", ETTERSENDING);

    private Counters() {

    }

}
