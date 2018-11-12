package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.CounterRegistry.FPFORDEL_SEND_INITIELL;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.CounterRegistry.FP_ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.CounterRegistry.FP_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.CounterRegistry.FP_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.CounterRegistry.FP_SENDFEIL;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

@Service
@Qualifier("fpfordel")
public class FPFordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelSøknadSender.class);

    private final FPFordelConnection connection;
    private final FPFordelKonvoluttGenerator konvoluttGenerator;

    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator konvoluttGenerator) {
        this.connection = connection;
        this.konvoluttGenerator = konvoluttGenerator;
    }

    public void ping() {
        LOG.info("Pinger");
        connection.ping();
    }

    @Override
    public Kvittering send(Endringssøknad endringsSøknad, Person søker) {
        Kvittering kvittering = send(ENDRING, konvoluttGenerator.payload(endringsSøknad, søker));
        FP_ENDRING.increment();
        return kvittering;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker) {
        Kvittering kvittering = send(INITIELL, konvoluttGenerator.payload(søknad, søker));
        FPFORDEL_SEND_INITIELL.increment();
        FP_FØRSTEGANG.increment();
        return kvittering;
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker) {
        Kvittering kvittering = send(ETTERSENDING, konvoluttGenerator.payload(ettersending, søker));
        FP_ETTERSSENDING.increment();
        return kvittering;
    }

    private Kvittering send(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        if (connection.isEnabled()) {
            return doSend(type, payload);
        }
        LOG.info("Sending av {} til FPFordel er deaktivert, ingenting å sende", type);
        return new Kvittering(IKKE_SENDT_FPSAK);
    }

    private Kvittering doSend(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        try {
            LOG.info("Sender {} til FPFordel", type.name().toLowerCase());
            Kvittering kvittering = connection.send(payload);
            LOG.info("Returnerer kvittering {}", kvittering);
            return kvittering;
        } catch (Exception e) {
            FP_SENDFEIL.increment();
            throw (e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", konvoluttGenerator=" + konvoluttGenerator
                + "]";
    }

}
