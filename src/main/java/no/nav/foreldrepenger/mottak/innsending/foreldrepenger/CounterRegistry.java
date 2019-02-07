package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL_FORELDREPENGER;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public final class CounterRegistry {

    private static final String TYPE = "type";
    private static final String YTELSE = "ytelse";
    private static final String FORELDREPENGER = "foreldrepenger";
    private static final String ENGANGSSTØNAD = "engangsstønad";
    private static final String DOKMOT_SEND = "dokmot.send";
    private static final String FPINFO_KVITTERINGER = "fpinfo.kvitteringer";
    private static final String FPFORDEL_KVITTERINGER = "fpfordel.kvitteringer";
    private static final String FPFORDEL_SEND = "fpfordel.send";

    public static final Counter DOKMOT_SUKSESS = Metrics.counter(DOKMOT_SEND, "søknad", "success");
    public static final Counter DOKMOT_FAILURE = Metrics.counter(DOKMOT_SEND, "søknad", "failure");

    public static final Counter ES_FØRSTEGANG = esCounter(FPFORDEL_SEND, INITIELL_ENGANGSSTØNAD.name());
    public static final Counter FP_SENDFEIL = fpCounter(FPFORDEL_SEND, "feil");
    public static final Counter FP_FØRSTEGANG = fpCounter(FPFORDEL_SEND, INITIELL_FORELDREPENGER.name());
    public static final Counter FP_ENDRING = fpCounter(FPFORDEL_SEND, ENDRING_FORELDREPENGER.name());
    public static final Counter FP_ETTERSSENDING = fpCounter(FPFORDEL_SEND, ETTERSENDING_FORELDREPENGER.name());
    public static final Counter GITTOPP_KVITTERING = fpCounter(FPFORDEL_KVITTERINGER, "gittopp");
    public static final Counter MANUELL_KVITTERING = fpCounter(FPFORDEL_KVITTERINGER, "gosys");
    public static final Counter FORDELT_KVITTERING = fpCounter(FPFORDEL_KVITTERINGER, "fordelt");
    public static final Counter FEILET_KVITTERINGER = fpCounter(FPFORDEL_KVITTERINGER, "feilet");
    public static final Counter PENDING = fpCounter(FPINFO_KVITTERINGER, "påvent");
    public static final Counter REJECTED = fpCounter(FPINFO_KVITTERINGER, "avslått");
    public static final Counter ACCEPTED = fpCounter(FPINFO_KVITTERINGER, "innvilget");
    public static final Counter RUNNING = fpCounter(FPINFO_KVITTERINGER, "pågår");
    public static final Counter FAILED = fpCounter(FPINFO_KVITTERINGER, "feilet");

    public static final Counter FPFORDEL_SEND_INITIELL = Metrics.counter("fpfordel_send_initiell");

    private CounterRegistry() {

    }

    private static Counter fpCounter(String name, String type) {
        return counter(name, FORELDREPENGER, type);
    }

    private static Counter esCounter(String name, String type) {
        return counter(name, ENGANGSSTØNAD, type);
    }

    private static Counter counter(String name, String ytelse, String type) {
        return Metrics.counter(name, YTELSE, ytelse, TYPE, type);
    }

}
