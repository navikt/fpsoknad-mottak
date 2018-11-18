package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public final class CounterRegistry {

    private static final String DOKMOT_SEND = "dokmot.send";
    private static final String FPINFO_KVITTERINGER = "fpinfo.kvitteringer";
    private static final String FPFORDEL_KVITTERINGER = "fpfordel.kvitteringer";
    private static final String FPFORDEL_SEND = "fpfordel.send";

    public static final Counter DOKMOT_SUKSESS = Metrics.counter(DOKMOT_SEND, "søknad", "success");
    public static final Counter DOKMOT_FAILURE = Metrics.counter(DOKMOT_SEND, "søknad", "failure");

    public static final Counter FP_SENDFEIL = fpcounter(FPFORDEL_SEND, "feil");
    public static final Counter FP_FØRSTEGANG = fpcounter(FPFORDEL_SEND, INITIELL.name());
    public static final Counter FP_ENDRING = fpcounter(FPFORDEL_SEND, ENDRING.name());
    public static final Counter FP_ETTERSSENDING = fpcounter(FPFORDEL_SEND, ETTERSENDING.name());
    public static final Counter GITTOPP_KVITTERING = fpcounter(FPFORDEL_KVITTERINGER, "gittopp");
    public static final Counter MANUELL_KVITTERING = fpcounter(FPFORDEL_KVITTERINGER, "gosys");
    public static final Counter FORDELT_KVITTERING = fpcounter(FPFORDEL_KVITTERINGER, "fordelt");
    public static final Counter FEILET_KVITTERINGER = fpcounter(FPFORDEL_KVITTERINGER, "feilet");
    public static final Counter PENDING = fpcounter(FPINFO_KVITTERINGER, "påvent");
    public static final Counter REJECTED = fpcounter(FPINFO_KVITTERINGER, "avslått");
    public static final Counter ACCEPTED = fpcounter(FPINFO_KVITTERINGER, "innvilget");
    public static final Counter RUNNING = fpcounter(FPINFO_KVITTERINGER, "pågår");
    public static final Counter FAILED = fpcounter(FPINFO_KVITTERINGER, "feilet");

    public static final Counter FPFORDEL_SEND_INITIELL = Metrics.counter("fpfordel_send_initiell");

    private CounterRegistry() {

    }

    private static Counter fpcounter(String name, String type) {
        return Metrics.counter(name, "ytelse", "foreldrepenger", "type", type);
    }

}
