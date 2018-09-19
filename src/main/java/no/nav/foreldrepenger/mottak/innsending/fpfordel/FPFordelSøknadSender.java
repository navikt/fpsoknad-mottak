package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;

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
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

@Service
@Qualifier("fpfordel")
public class FPFordelSøknadSender implements SøknadSender {

    private static final String VEDLEGG = "vedlegg";

    private static final String SØKNAD = "søknad";

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
    public Kvittering send(Søknad søknad, Person søker) {
        String ref = callIdGenerator.getOrCreate();
        return send(SØKNAD, ref, konvoluttGenerator.payload(søknad, søker, ref));
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person person) {
        String ref = callIdGenerator.getOrCreate();
        return send(VEDLEGG, ref, konvoluttGenerator.payload(ettersending, person, ref));
    }

    private Kvittering send(String type, String ref, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        if (connection.isEnabled()) {
            LOG.info("Sender {} til FPFordel", type.toLowerCase());
            Kvittering kvittering = connection.send(payload, ref);
            LOG.info("Returnerer kvittering {}", kvittering);
            return kvittering;
        }
        LOG.info("Sending av {} til FPFordel er deaktivert, ingenting å sende", type);
        return new Kvittering(IKKE_SENDT_FPSAK, ref);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", callIdGenerator=" + callIdGenerator
                + ", konvoluttGenerator=" + konvoluttGenerator + "]";
    }

}
