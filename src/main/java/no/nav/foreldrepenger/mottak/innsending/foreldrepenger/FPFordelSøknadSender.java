package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.http.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.http.CounterRegistry.FP_ENDRING;
import static no.nav.foreldrepenger.mottak.http.CounterRegistry.FP_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.http.CounterRegistry.FP_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ENDRING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.ETTERSENDING;
import static no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType.INITIELL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
import no.nav.foreldrepenger.mottak.http.errorhandling.SendException;

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
        String ref = MDC.get(NAV_CALL_ID);
        Kvittering kvittering = send(ENDRING, ref, konvoluttGenerator.payload(endringsSøknad, søker, ref));
        FP_ENDRING.increment();
        return kvittering;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker) {
        String ref = MDC.get(NAV_CALL_ID);
        Kvittering kvittering = send(INITIELL, ref, konvoluttGenerator.payload(søknad, søker, ref));
        FP_FØRSTEGANG.increment();
        return kvittering;
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker) {
        String ref = MDC.get(NAV_CALL_ID);
        Kvittering kvittering = send(ETTERSENDING, ref, konvoluttGenerator.payload(ettersending, søker, ref));
        FP_ETTERSSENDING.increment();
        return kvittering;
    }

    private Kvittering send(SøknadType type, String ref, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        if (connection.isEnabled()) {
            return doSend(type, ref, payload);
        }
        LOG.info("Sending av {} til FPFordel er deaktivert, ingenting å sende", type);
        return new Kvittering(IKKE_SENDT_FPSAK, ref);
    }

    private Kvittering doSend(SøknadType type, String ref, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        try {
            LOG.info("Sender {} til FPFordel", type.name().toLowerCase());
            Kvittering kvittering = connection.send(payload, ref);
            LOG.info("Returnerer kvittering {}", kvittering);
            return kvittering;
        } catch (Exception e) {
            throw new SendException(type, e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", konvoluttGenerator=" + konvoluttGenerator
                + "]";
    }

}
