package no.nav.foreldrepenger.mottak.innsending.dokmot;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;

import javax.jms.TextMessage;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

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
    public Kvittering sendSøknad(Søknad søknad, Person søker) {
        if (dokmotConnection.isEnabled()) {
            String ref = idGenerator.getOrCreate();
            dokmotConnection.send(session -> {
                LOG.info("Sender SøknadsXML til DOKMOT");
                TextMessage msg = session.createTextMessage(generator.toXML(søknad, søker, ref));
                msg.setStringProperty("callId", ref);
                return msg;
            });
            return new Kvittering(ref);
        }
        LOG.info("Leveranse til DOKMOT er deaktivert, ingenting å sende");
        return IKKE_SENDT;
    }

    @Override
    public Kvittering sendEttersending(@Valid Ettersending ettersending, Person søker) {
        throw new IllegalArgumentException("Ettersending for engangsstønad ikke implementert");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotConnection + ", generator=" + generator
                + ", callIdGenerator=" + idGenerator + "]";
    }

}
