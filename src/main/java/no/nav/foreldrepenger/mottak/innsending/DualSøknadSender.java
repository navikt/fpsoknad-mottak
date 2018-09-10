package no.nav.foreldrepenger.mottak.innsending;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.innsending.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelSøknadSender;

@Service
@Qualifier("dual")
public class DualSøknadSender implements SøknadSender {

    private final DokmotJMSSender dokmotSender;
    private final FPFordelSøknadSender fpfordelSender;

    public DualSøknadSender(DokmotJMSSender dokmotSender, FPFordelSøknadSender fpfordelSender) {
        this.dokmotSender = dokmotSender;
        this.fpfordelSender = fpfordelSender;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker) {
        return isForeldrepenger(søknad) ? fpfordelSender.send(søknad, søker) : dokmotSender.send(søknad, søker);
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker) {
        return fpfordelSender.send(ettersending, søker);
    }

    private static boolean isForeldrepenger(Søknad søknad) {
        return søknad.getYtelse() instanceof Foreldrepenger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotSender=" + dokmotSender + ", fpfordelSender=" + fpfordelSender
                + "]";
    }
}
