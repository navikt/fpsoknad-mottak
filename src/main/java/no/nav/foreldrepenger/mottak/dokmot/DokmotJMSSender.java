package no.nav.foreldrepenger.mottak.dokmot;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;

import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;

@Service
public class DokmotJMSSender implements SøknadSender {

    private final DokmotConnection dokmotConnection;
    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;
    private final UUIDIdGenerator idGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    public DokmotJMSSender(DokmotConnection connection, DokmotEngangsstønadXMLKonvoluttGenerator generator,
            UUIDIdGenerator callIdGenerator) {
        this.dokmotConnection = connection;
        this.generator = generator;
        this.idGenerator = callIdGenerator;
    }

    @Override
    public Kvittering sendSøknad(Søknad søknad, AktorId aktorId) {
        if (dokmotConnection.isEnabled()) {
            String ref = idGenerator.getOrCreate();
            dokmotConnection.send(session -> {
                LOG.info("Sender SøknadsXML til DOKMOT");
                TextMessage msg = session.createTextMessage(generator.toXML(søknad, ref));
                msg.setStringProperty("callId", ref);
                return msg;
            });
            return new Kvittering(ref);
        }
        LOG.info("Leveranse til DOKMOT er deaktivert, ingenting å sende");
        return IKKE_SENDT;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotConnection + ", generator=" + generator
                + ", callIdGenerator=" + idGenerator + "]";
    }

}
