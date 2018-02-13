package no.nav.foreldrepenger.mottak;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
@EnableJms
@ComponentScan("no.nav.foreldrepenger")
public class MottakApplication {

    public static void main(String[] args) {
        BeanFactory context = SpringApplication.run(MottakApplication.class, args);
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
    }
}
