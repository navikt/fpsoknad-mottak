package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_FAILED;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_SUCCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class VarselConnection {

    private static final Logger LOG = LoggerFactory.getLogger(VarselConnection.class);

    private final JmsTemplate template;
    private final VarselQueueConfig queueConfig;

    public VarselConnection(@Qualifier("varselTemplate") JmsTemplate template, VarselQueueConfig queueConfig) {
        this.template = template;
        this.queueConfig = queueConfig;
    }

    public void ping() {
        LOG.info("Pinger {}", queueConfig.loggable());
        try {
            template.getConnectionFactory().createConnection().close();
        } catch (JMSException swallow) {
            LOG.warn("Kunne ikke pinge VARSEL-kø {}", queueConfig.loggable(), swallow);
            //TODO: legg til varsel som en Health-indicator?
        }
    }

    public void send(MessageCreator msg) {
        try {
            template.send(msg);
            VARSEL_SUCCESS.increment();
        } catch (JmsException swallow) {
            LOG.error("Feil ved sending til Varseltjenesten {}", queueConfig.loggable(), swallow);
            VARSEL_FAILED.increment();
        }
    }

    public VarselQueueConfig getQueueConfig() {
        return queueConfig;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", queueConfig=" + queueConfig.loggable() + "]";
    }

}
