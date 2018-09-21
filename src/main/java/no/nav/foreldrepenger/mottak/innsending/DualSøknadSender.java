package no.nav.foreldrepenger.mottak.innsending;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;

@Service
@Qualifier("dual")
public class DualSøknadSender implements SøknadSender {

    private final SøknadSender dokmot;
    private final SøknadSender fpfordel;

    public DualSøknadSender(@Qualifier("dokmot") SøknadSender dokmot, @Qualifier("fpfordel") SøknadSender fpfordel) {
        this.dokmot = dokmot;
        this.fpfordel = fpfordel;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker) {
        return isForeldrepenger(søknad) ? fpfordel.send(søknad, søker) : dokmot.send(søknad, søker);
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker) {
        return fpfordel.send(ettersending, søker);
    }

    private static boolean isForeldrepenger(Søknad søknad) {
        return søknad.getYtelse() instanceof Foreldrepenger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmot=" + dokmot + ", fpfordel=" + fpfordel + "]";
    }
}
