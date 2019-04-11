package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Service
public class FPFordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelSøknadSender.class);

    private final FPFordelConnection connection;
    private final FPFordelKonvoluttGenerator generator;

    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator generator) {
        this.connection = connection;
        this.generator = generator;
    }

    @Override
    public Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        Kvittering kvittering = doSend(egenskap, søknad.getSøknadsRolle(), generator.generer(søknad, søker, egenskap));
        kvittering.setFørsteDag(søknad.getFørsteUttaksdag());
        return kvittering;
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endringssøknad, Person søker, SøknadEgenskap egenskap) {
        Kvittering kvittering = doSend(egenskap, endringssøknad.getSøknadsRolle(),
                generator.generer(endringssøknad, søker, egenskap));
        kvittering.setFørsteDag(endringssøknad.getFørsteUttaksdag());
        return kvittering;
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return doSend(egenskap, null, generator.generer(ettersending, søker));
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    private Kvittering doSend(SøknadEgenskap egenskap, BrukerRolle rolle, FPFordelKonvolutt konvolutt) {
        if (skalSende(egenskap)) {
            return connection.send(egenskap.getType(), rolle, konvolutt);
        }
        LOG.warn("Sender ikke {}", egenskap);
        return new Kvittering(IKKE_SENDT_FPSAK);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", generator=" + generator + "]";
    }
}
