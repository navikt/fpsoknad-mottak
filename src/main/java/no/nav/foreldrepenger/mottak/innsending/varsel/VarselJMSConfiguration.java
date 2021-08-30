package no.nav.foreldrepenger.mottak.innsending.varsel;

import static com.ibm.mq.constants.CMQC.MQENC_NATIVE;
import static com.ibm.msg.client.jms.JmsConstants.JMS_IBM_CHARACTER_SET;
import static com.ibm.msg.client.jms.JmsConstants.JMS_IBM_ENCODING;
import static com.ibm.msg.client.wmq.common.CommonConstants.WMQ_CM_CLIENT;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.ibm.mq.jms.MQQueueConnectionFactory;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnK8s;

@Configuration
@ConditionalOnK8s
public class VarselJMSConfiguration {

    private static final int UTF_8_WITH_PUA = 1208;

    @Bean
    public JmsTemplate varselTemplate(VarselConfig cfg, ConnectionFactory cf) {
        var jmsTemplate = new JmsTemplate(cf);
        jmsTemplate.setDefaultDestinationName(cfg.getQueueName());
        jmsTemplate.setDestinationResolver(new DynamicDestinationResolver());
        return jmsTemplate;
    }

    @Bean
    ConnectionFactory connectionFactory(VarselConfig cfg) throws JMSException {
        var cf = new UserCredentialsConnectionFactoryAdapter();
        cf.setUsername(cfg.getUsername());
        cf.setTargetConnectionFactory(targetFrom(cfg));
        return cf;
    }

    private static ConnectionFactory targetFrom(VarselConfig cfg) throws JMSException {
        var cf = new MQQueueConnectionFactory();
        cf.setHostName(cfg.getHostname());
        cf.setPort(cfg.getPort());
        cf.setChannel(cfg.getChannelname());
        cf.setQueueManager(cfg.getName());
        cf.setTransportType(WMQ_CM_CLIENT);
        cf.setCCSID(UTF_8_WITH_PUA);
        cf.setIntProperty(JMS_IBM_ENCODING, MQENC_NATIVE);
        cf.setIntProperty(JMS_IBM_CHARACTER_SET, UTF_8_WITH_PUA);
        return cf;
    }

}
