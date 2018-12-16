package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;
import static no.nav.foreldrepenger.mottak.domain.SøknadSender.FPFORDEL;
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
import no.nav.foreldrepenger.mottak.util.Versjon;

@Service
@Qualifier(FPFORDEL)
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
    public Kvittering send(Endringssøknad endringsSøknad, Person søker, Versjon versjon) {
        return send(ENDRING, konvoluttGenerator.payload(endringsSøknad, søker, versjon));
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker, Versjon versjon) {
        return send(INITIELL, konvoluttGenerator.payload(søknad, søker, versjon));
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker, Versjon versjon) {
        return send(ETTERSENDING, konvoluttGenerator.payload(ettersending, søker));
    }

    private Kvittering send(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        if (connection.isEnabled()) {
            return doSend(type, payload);
        }
        LOG.info("Sending av {} til FPFordel er deaktivert, ingenting å sende", type);
        return IKKE_SENDT;
    }

    private Kvittering doSend(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        try {
            logAndCount(type);
            Kvittering kvittering = connection.send(payload);
            LOG.info("Returnerer kvittering {}", kvittering);
            return kvittering;
        } catch (Exception e) {
            FP_SENDFEIL.increment();
            throw (e);
        }
    }

    private static void logAndCount(SøknadType type) {
        LOG.info("Sender {} til FPFordel", type.name().toLowerCase());
        switch (type) {
        case ENDRING:
            FP_ENDRING.increment();
            break;
        case ETTERSENDING:
            FP_ETTERSSENDING.increment();
            break;
        case INITIELL:
            FPFORDEL_SEND_INITIELL.increment();
            FP_FØRSTEGANG.increment();
            break;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", konvoluttGenerator=" + konvoluttGenerator
                + "]";
    }

}
