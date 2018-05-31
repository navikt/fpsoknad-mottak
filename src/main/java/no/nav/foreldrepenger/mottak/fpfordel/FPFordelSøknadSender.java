package no.nav.foreldrepenger.mottak.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

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
    public Kvittering sendSøknad(Søknad søknad, AktorId aktørId) {
        String ref = idGenerator.getOrCreate();
        return send("søknad", ref, generator.payload(søknad, aktørId, ref), aktørId);
    }

    @Override
    public Kvittering sendEttersending(@Valid Ettersending ettersending, AktorId aktørId) {
        String ref = idGenerator.getOrCreate();
        return send("vedlegg", ref, generator.payload(ettersending, aktørId, ref), aktørId);
    }

    private Kvittering send(String type, String ref, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload,
            AktorId aktørId) {
        if (connection.isEnabled()) {
            LOG.info("Sender {} til FPFordel", type);
            return new Kvittering(ref, connection.send(payload));
        }
        LOG.info("Sendning av {} til FPFordel er deaktivert, ingenting å sende", type);
        return IKKE_SENDT;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", idGenerator=" + idGenerator
                + ", generator=" + generator + "]";
    }

}
