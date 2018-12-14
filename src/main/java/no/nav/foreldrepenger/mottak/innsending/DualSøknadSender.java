package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.domain.Søknad.DEFAULT_VERSJON;
import static no.nav.foreldrepenger.mottak.domain.SøknadSender.DUAL;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.VersjonerbarSøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Service
@Qualifier(DUAL)
public class DualSøknadSender implements VersjonerbarSøknadSender {

    private final SøknadSender dokmot;
    private final VersjonerbarSøknadSender fpfordel;

    public DualSøknadSender(@Qualifier(DOKMOT) SøknadSender dokmot,
            @Qualifier(FPFORDEL) VersjonerbarSøknadSender fpfordel) {
        this.dokmot = dokmot;
        this.fpfordel = fpfordel;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker) {
        return send(søknad, søker, DEFAULT_VERSJON);
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker, Versjon versjon) {
        return isForeldrepenger(søknad) ? fpfordel.send(søknad, søker, versjon) : dokmot.send(søknad, søker);
    }

    @Override
    public Kvittering send(Endringssøknad endringsøknad, Person søker) {
        return send(endringsøknad, søker, DEFAULT_VERSJON);

    }

    @Override
    public Kvittering send(Endringssøknad endringssøknad, Person søker, Versjon versjon) {
        return isForeldrepenger(endringssøknad) ? fpfordel.send(endringssøknad, søker, versjon)
                : dokmot.send(endringssøknad, søker);
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
