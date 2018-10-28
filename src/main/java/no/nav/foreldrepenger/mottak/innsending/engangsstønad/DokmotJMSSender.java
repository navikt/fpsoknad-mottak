package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.http.Constants.NAV_CALL_ID;

import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;

@Service
@Qualifier("dokmot")
public class DokmotJMSSender implements SøknadSender {

    private final DokmotConnection dokmotConnection;
    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;
    private final CallIdGenerator idGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    public DokmotJMSSender(DokmotConnection connection, DokmotEngangsstønadXMLKonvoluttGenerator generator,
            CallIdGenerator callIdGenerator) {
        this.dokmotConnection = connection;
        this.generator = generator;
        this.idGenerator = callIdGenerator;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker) {
        if (dokmotConnection.isEnabled()) {
            String ref = MDC.get(NAV_CALL_ID);
            dokmotConnection.send(session -> {
                TextMessage msg = session.createTextMessage(generator.tilXML(søknad, søker, ref));
                LOG.info("Sender SøknadsXML til DOKMOT");
                msg.setStringProperty("callId", ref);
                return msg;
            });
            return new Kvittering(PÅ_VENT, ref);
        }
        LOG.info("Leveranse til DOKMOT er deaktivert, ingenting å sende");
        return new Kvittering(IKKE_SENDT_FPSAK, "42");
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker) {
        throw new IllegalArgumentException("Ettersending for engangsstønad via DOKMOT er ikke støttet");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotConnection + ", generator=" + generator
                + ", callIdGenerator=" + idGenerator + "]";
    }

    @Override
    public Kvittering send(Endringssøknad endringsøknad, Person søker) {
        throw new IllegalArgumentException("Sending av endringssøknad via DOKMOT er ikke støttet");
    }

}
