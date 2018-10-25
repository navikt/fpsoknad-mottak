package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public final class Counters {

    public static final Counter TELLER_SENDFEIL = Metrics.counter("fpfordel.send", "ytelse",
            "foreldrepenger", "type", "feil");

    public static final Counter TELLER_FØRSTEGANG = Metrics.counter("fpfordel.send", "ytelse", "foreldrepenger", "type",
            INITIELL.name());
    public static final Counter TELLER_ENDRING = Metrics.counter("fpfordel.send", "ytelse", "foreldrepenger", "type",
            ENDRING.name());
    public static final Counter TELLER_ETTERSSENDING = Metrics.counter("fpfordel.send", "ytelse", "foreldrepenger",
            "type", ETTERSENDING.name());

    private Counters() {

    }

}
