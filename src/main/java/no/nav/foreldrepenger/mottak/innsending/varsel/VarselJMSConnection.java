package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.mottak.Constants.CALL_ID;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_FAILED;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_SUCCESS;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;

import java.net.URI;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "varsel.", name = "enabled", havingValue = "true")
public class VarselJMSConnection implements VarselConnection {

    private static final Logger LOG = LoggerFactory.getLogger(VarselJMSConnection.class);

    private final JmsTemplate template;
    private final VarselQueueConfig queueConfig;

    public VarselJMSConnection(JmsTemplate template, VarselQueueConfig queueConfig) {
        this.template = template;
        this.queueConfig = queueConfig;
    }

    @Override
    public String ping() {
        LOG.info("Pinger {} ({})", name(), queueConfig.getURI());
        try {
            template.getConnectionFactory().createConnection().close();
            return (name() + " is alive and kicking at " + pingEndpoint());
        } catch (JMSException e) {
            LOG.warn("Kunne ikke pinge {}-kø ({})", name(), queueConfig.getURI(), e);
            throw new IllegalArgumentException("Kunne ikke pinge " + name() + "-kø", e);
        }
    }

    @Override
    public URI pingEndpoint() {
        return queueConfig.getURI();
    }

    @Override
    public String name() {
        return "varseltjeneste";
    }

    @Override
    public void send(String xml) {
        LOG.info("Legger melding for varsel på {}-kø ({})", name(), queueConfig.getURI());
        try {
            template.send(session -> {
                TextMessage msg = session.createTextMessage(xml);
                msg.setStringProperty(CALL_ID, callId());
                return msg;
            });
            VARSEL_SUCCESS.increment();
        } catch (JmsException swallow) {
            LOG.error("Feil ved sending av varsel til {}-kø ({})", name(), queueConfig.getURI(), swallow);
            VARSEL_FAILED.increment();
        }
    }

    public VarselQueueConfig getQueueConfig() {
        return queueConfig;
    }

    @Override
    public boolean isEnabled() {
        return getQueueConfig().isEnabled();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", queueConfig=" + queueConfig.getURI() + "]";
    }

}
