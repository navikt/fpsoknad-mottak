package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.http.Counters.TELLER_ENDRING;
import static no.nav.foreldrepenger.mottak.http.Counters.TELLER_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.http.Counters.TELLER_FØRSTEGANG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
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
    private final CallIdGenerator callIdGenerator;
    private final FPFordelKonvoluttGenerator konvoluttGenerator;

    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator konvoluttGenerator,
            CallIdGenerator callIdGenerator) {
        this.connection = connection;
        this.konvoluttGenerator = konvoluttGenerator;
        this.callIdGenerator = callIdGenerator;
    }

    public void ping() {
        LOG.info("Pinger");
        connection.ping();
    }

    @Override
    public Kvittering send(Endringssøknad endringsSøknad, Person søker) {
        String ref = callIdGenerator.getOrCreate();
        Kvittering kvittering = send("endring", ref, konvoluttGenerator.payload(endringsSøknad, søker, ref));
        TELLER_ENDRING.increment();
        return kvittering;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker) {
        String ref = callIdGenerator.getOrCreate();
        Kvittering kvittering = send("initiell", ref, konvoluttGenerator.payload(søknad, søker, ref));
        TELLER_FØRSTEGANG.increment();
        return kvittering;
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker) {
        String ref = callIdGenerator.getOrCreate();
        Kvittering kvittering = send("ettersending", ref, konvoluttGenerator.payload(ettersending, søker, ref));
        TELLER_ETTERSSENDING.increment();
        return kvittering;
    }

    private Kvittering send(String type, String ref, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        if (connection.isEnabled()) {
            return doSend(type, ref, payload);
        }
        LOG.info("Sending av {} til FPFordel er deaktivert, ingenting å sende", type);
        return new Kvittering(IKKE_SENDT_FPSAK, ref);
    }

    private Kvittering doSend(String type, String ref, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        try {
            LOG.info("Sender {} til FPFordel", type.toLowerCase());
            Kvittering kvittering = connection.send(payload, ref);
            LOG.info("Returnerer kvittering {}", kvittering);
            return kvittering;
        } catch (Exception e) {
            throw new SendException(type, e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", callIdGenerator=" + callIdGenerator
                + ", konvoluttGenerator=" + konvoluttGenerator + "]";
    }

}
