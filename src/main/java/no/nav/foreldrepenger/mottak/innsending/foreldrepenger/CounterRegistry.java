package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public final class CounterRegistry {

    private static final String FPFORDEL_KVITTERINGER = "fpfordel.kvitteringer";
    private static final String SFPFORDEL_SEND = "fpfordel.send";

    public static final Counter FP_SENDFEIL = counter(SFPFORDEL_SEND, "feil");
    public static final Counter FP_FØRSTEGANG = counter(SFPFORDEL_SEND, INITIELL.name());
    public static final Counter FP_ENDRING = counter(SFPFORDEL_SEND, ENDRING.name());
    public static final Counter FP_ETTERSSENDING = counter(SFPFORDEL_SEND, ETTERSENDING.name());
    public static final Counter GITTOPP_KVITTERING = counter(FPFORDEL_KVITTERINGER, "gittopp");
    public static final Counter MANUELL_KVITTERING = counter(FPFORDEL_KVITTERINGER, "gosys");
    public static final Counter FORDELT_KVITTERING = counter(FPFORDEL_KVITTERINGER, "fordelt");
    public static final Counter FEILET_KVITTERINGER = counter(FPFORDEL_KVITTERINGER, "feilet");

    private CounterRegistry() {

    }

    private static Counter counter(String name, String type) {
        Counter counter = Metrics.counter(name, "ytelse", "foreldrepenger", "type", type);
        counter.increment(0);
        return counter;
    }

}
