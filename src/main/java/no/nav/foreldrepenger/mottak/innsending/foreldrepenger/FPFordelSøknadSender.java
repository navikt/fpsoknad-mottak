package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Service
@ConditionalOnProperty(name = "svangerskapspenger.enabled", havingValue = "true", matchIfMissing = true)
public class FPFordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelSøknadSender.class);

    private final FPFordelConnection connection;
    private final FPFordelKonvoluttGenerator payloadGenerator;

    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator payloadGenerator) {
        this.connection = connection;
        this.payloadGenerator = payloadGenerator;
    }

    @Override
    public Kvittering søk(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return doSend(egenskap, søknad.getSøknadsRolle(), payloadGenerator.generer(søknad, søker, egenskap));
    }

    @Override
    public Kvittering endreSøknad(Endringssøknad endringssøknad, Person søker, SøknadEgenskap egenskap) {
        return doSend(egenskap, endringssøknad.getSøknadsRolle(),
                payloadGenerator.generer(endringssøknad, søker, egenskap));
    }

    @Override
    public Kvittering ettersend(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return doSend(egenskap, null,
                payloadGenerator.generer(ettersending, søker));
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
        return new Kvittering(LeveranseStatus.IKKE_SENDT_FPSAK);
    }

    @Override
    public boolean skalSende(SøknadEgenskap egenskap) {
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", payloadGenerator=" + payloadGenerator
                + "]";
    }

}
