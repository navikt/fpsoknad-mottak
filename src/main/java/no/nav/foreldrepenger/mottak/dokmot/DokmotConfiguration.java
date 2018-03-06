package no.nav.foreldrepenger.mottak.dokmot;

import static com.ibm.mq.constants.CMQC.MQENC_NATIVE;
import static com.ibm.msg.client.jms.JmsConstants.JMS_IBM_CHARACTER_SET;
import static com.ibm.msg.client.jms.JmsConstants.JMS_IBM_ENCODING;
import static com.ibm.msg.client.wmq.common.CommonConstants.WMQ_CM_CLIENT;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.ibm.mq.jms.MQQueueConnectionFactory;

@Configuration
public class DokmotConfiguration {

    private static final int UTF_8_WITH_PUA = 1208;

    @Bean
    public JmsTemplate dokmotTemplate(ConnectionFactory cf,
            @Value("${DOKMOT_MOTTA_FORSENDELSE_DITT_NAV_QUEUENAME}") String queueName) {
        JmsTemplate jmsTemplate = new JmsTemplate(cf);
        jmsTemplate.setDefaultDestinationName(queueName);
        jmsTemplate.setDestinationResolver(new DynamicDestinationResolver());
        return jmsTemplate;
    }

    @Bean
    public MQQueueConnectionFactory connectionFactory(@Value("${MQGATEWAY01_HOSTNAME}") String host,
            @Value("${MQGATEWAY01_PORT}") int port, @Value("${MQGATEWAY01_NAME}") String queueManager,
            @Value("${DOKMOT_CHANNEL_NAME}") String channel) throws JMSException {

        MQQueueConnectionFactory cf = new MQQueueConnectionFactory();
        cf.setHostName(host);
        cf.setTransportType(WMQ_CM_CLIENT);
        cf.setCCSID(UTF_8_WITH_PUA);
        cf.setPort(port);
        cf.setChannel(channel);
        cf.setQueueManager(queueManager);
        cf.setIntProperty(JMS_IBM_ENCODING, MQENC_NATIVE);
        cf.setIntProperty(JMS_IBM_CHARACTER_SET, UTF_8_WITH_PUA);
        return cf;
    }

    @Bean
    @Primary
    ConnectionFactory userCredentialsConnectionFactoryAdapter(MQQueueConnectionFactory delegate,
            @Value("${DOKMOT_USERNAME}") String username) {
        UserCredentialsConnectionFactoryAdapter cf = new UserCredentialsConnectionFactoryAdapter();
        cf.setUsername(username);
        cf.setTargetConnectionFactory(delegate);
        return cf;
    }
}
