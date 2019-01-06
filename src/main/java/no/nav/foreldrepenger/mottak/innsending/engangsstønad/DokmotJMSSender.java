package no.nav.foreldrepenger.mottak.innsending.engangsstønad;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;
import static no.nav.foreldrepenger.mottak.http.Constants.CALL_ID;
import static no.nav.foreldrepenger.mottak.innsending.SøknadSender.DOKMOT;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;

import javax.jms.TextMessage;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Service
@Qualifier(DOKMOT)
public class DokmotJMSSender implements SøknadSender {

    private final DokmotConnection connection;
    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    public DokmotJMSSender(DokmotConnection connection, DokmotEngangsstønadXMLKonvoluttGenerator generator) {
        this.connection = connection;
        this.generator = generator;
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker, Versjon versjon) {
        if (connection.isEnabled()) {
            connection.send(session -> {
                TextMessage msg = session.createTextMessage(generator.tilXML(søknad, søker));
                LOG.info("Sender SøknadsXML til DOKMOT");
                msg.setStringProperty(CALL_ID, callId());
                return msg;
            });
            return new Kvittering(LeveranseStatus.PÅ_VENT);
        }
        LOG.info("Leveranse til DOKMOT er deaktivert, ingenting å sende");
        return IKKE_SENDT;
    }

    @Override
    public Kvittering send(Endringssøknad endringsøknad, Person søker, Versjon versjon) {
        throw new NotImplementedException("Sending av endringssøknad via DOKMOT er ikke støttet");
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker, Versjon versjon) {
        throw new NotImplementedException("Ettersending for engangsstønad via DOKMOT er ikke støttet");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + connection + ", generator=" + generator + "]";
    }

}
