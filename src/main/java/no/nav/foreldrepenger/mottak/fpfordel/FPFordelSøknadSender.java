package no.nav.foreldrepenger.mottak.fpfordel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;

@Service
public class FPFordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelSøknadSender.class);

    private final FPFordelConnection connection;
    private final UUIDIdGenerator idGenerator;
    private final FPFordelKonvoluttGenerator generator;

    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator generator,
            UUIDIdGenerator idGenerator) {
        this.connection = connection;
        this.generator = generator;
        this.idGenerator = idGenerator;
    }

    public void ping() {
        LOG.info("Pinger");
        connection.ping();
    }

    @Override
    public Kvittering sendSøknad(Søknad søknad, AktorId aktorId) {
        if (connection.isEnabled()) {
            String ref = idGenerator.getOrCreate();
            connection.send(generator.createPayload(søknad, aktorId, ref));
            return new Kvittering(ref);
        }
        return Kvittering.IKKE_SENDT;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }

}
