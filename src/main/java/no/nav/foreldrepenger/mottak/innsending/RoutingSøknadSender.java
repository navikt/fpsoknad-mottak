package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.innsending.SøknadSender.ROUTING_SENDER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.DOKMOT_ENGANGSSTØNAD;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Service
@Qualifier(ROUTING_SENDER)
public class RoutingSøknadSender implements SøknadSender {

    private final SøknadSender dokmot;
    private final SøknadSender fpfordel;

    public RoutingSøknadSender(@Qualifier(DOKMOT_SENDER) SøknadSender dokmot,
            @Qualifier(FPFORDEL_SENDER) SøknadSender fpfordel) {
        this.dokmot = dokmot;
        this.fpfordel = fpfordel;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        if (tilDokmot(egenskap)) {
            return dokmot.send(søknad, søker, egenskap);
        }
        return fpfordel.send(søknad, søker, egenskap);
    }

    @Override
    public Kvittering send(Endringssøknad endringssøknad, Person søker, SøknadEgenskap egenskap) {
        return fpfordel.send(endringssøknad, søker, egenskap);
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return fpfordel.send(ettersending, søker, egenskap);
    }

    private static boolean tilDokmot(SøknadEgenskap egenskap) {
        return DOKMOT_ENGANGSSTØNAD.equals(egenskap);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmot=" + dokmot + ", fpfordel=" + fpfordel + "]";
    }
}
