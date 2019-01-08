package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.innsending.SøknadSender.ROUTING_SENDER;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Service
@Qualifier(ROUTING_SENDER)
public class DualSøknadSender implements SøknadSender {

    private final SøknadSender dokmot;
    private final SøknadSender fpfordel;

    public DualSøknadSender(@Qualifier(DOKMOT_SENDER) SøknadSender dokmot,
            @Qualifier(FPFORDEL_SENDER) SøknadSender fpfordel) {
        this.dokmot = dokmot;
        this.fpfordel = fpfordel;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker, Versjon versjon) {
        return isForeldrepenger(søknad) ? fpfordel.send(søknad, søker, versjon) : dokmot.send(søknad, søker, versjon);
    }

    @Override
    public Kvittering send(Endringssøknad endringssøknad, Person søker, Versjon versjon) {
        return isForeldrepenger(endringssøknad) ? fpfordel.send(endringssøknad, søker, versjon)
                : dokmot.send(endringssøknad, søker, versjon);
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker, Versjon versjon) {
        return fpfordel.send(ettersending, søker, versjon);
    }

    private static boolean isForeldrepenger(Søknad søknad) {
        return søknad.getYtelse() instanceof Foreldrepenger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmot=" + dokmot + ", fpfordel=" + fpfordel + "]";
    }

}
