package no.nav.foreldrepenger.mottak;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.mottak.dokmot.JmsDokmotSender;

@SpringBootApplication
@ComponentScan("no.nav.foreldrepenger")
// @EnableJms
public class MottakApplication {

    public static void main(String[] args) {
        BeanFactory context = SpringApplication.run(MottakApplication.class, args);
        System.out.println("ping is " + context.getBean(JmsDokmotSender.class).ping());
        // context.getBean(JmsTemplate.class).send("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", textMessage("hello
        // world"));
        // ("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", "hei");
    }

}
