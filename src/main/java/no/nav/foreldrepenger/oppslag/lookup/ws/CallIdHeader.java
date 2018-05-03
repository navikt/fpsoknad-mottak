package no.nav.foreldrepenger.oppslag.lookup.ws;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.oppslag.lookup.UUIDCallIdGenerator;

public class CallIdHeader extends AbstractPhaseInterceptor<Message> {

    private static final Logger logger = LoggerFactory.getLogger(CallIdHeader.class);
    private final UUIDCallIdGenerator generator;

    public CallIdHeader(UUIDCallIdGenerator generator) {
        super(Phase.PRE_STREAM);
        this.generator = generator;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        try {
            QName qName = new QName("uri:no.nav.applikasjonsrammeverk", "callId");
            SoapHeader header = new SoapHeader(qName, generator.getOrCreate(), new JAXBDataBinding(String.class));
            ((SoapMessage) message).getHeaders().add(header);
        } catch (JAXBException ex) {
            logger.warn("Error while setting CallId header", ex);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + "]";
    }
}
