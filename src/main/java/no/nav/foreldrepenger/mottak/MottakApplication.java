package no.nav.foreldrepenger.mottak;

import static com.ibm.mq.constants.CMQC.MQENC_NATIVE;
import static com.ibm.msg.client.jms.JmsConstants.JMS_IBM_CHARACTER_SET;
import static com.ibm.msg.client.jms.JmsConstants.JMS_IBM_ENCODING;
import static com.ibm.msg.client.wmq.common.CommonConstants.WMQ_CM_CLIENT;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.ibm.mq.jms.MQQueueConnectionFactory;

@SpringBootApplication
@ComponentScan("no.nav.foreldrepenger")
// @EnableJms
public class MottakApplication {

    private static final int UTF_8_WITH_PUA = 1208;

    public static void main(String[] args) {
        BeanFactory context = SpringApplication.run(MottakApplication.class, args);
        // System.out.println("ping is " + context.getBean(JmsDokmotSender.class).ping());x'
        // context.getBean(JmsTemplate.class).send("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", textMessage("hello
        // world"));
        // ("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", "hei");
    }

    private static MessageCreator textMessage(final String msg) {
        return session -> {
            return session.createTextMessage(msg);
        };
    }

    @Bean
    public JmsTemplate dokmotTemplate(ConnectionFactory cf) {
        JmsTemplate jmsTemplate = new JmsTemplate(cf);
        jmsTemplate.setDestinationResolver(new DynamicDestinationResolver());
        return jmsTemplate;
    }

    @Bean
    public MQQueueConnectionFactory connectionFactory(@Value("${MQGATEWAY01_HOSTNAME}") String host,
            @Value("${MQGATEWAY01_PORT}") int port, @Value("${MQGATEWAY01_NAME}") String queueManager,
            @Value("${DOKMOT_CHANNEL_NAME}") String channel)
            throws JMSException {

        // dokmot_MOTTA_FORSENDELSE_DITT_NAV
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
            @Value("${BRISDOKMOT_USERNAME}") String username, @Value("${BRISDOKMOT_PASSWORD}") String password) {
        UserCredentialsConnectionFactoryAdapter cf = new UserCredentialsConnectionFactoryAdapter();
        cf.setUsername(username);
        cf.setPassword(password);
        cf.setTargetConnectionFactory(delegate);
        return cf;
    }

}
