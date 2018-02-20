package no.nav.foreldrepenger.mottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MottakApplication {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplication.class, args);
        // context.getBean(JmsTemplate.class).send("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", textMessage("hello
        // world"));
        // ("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", "hei");
    }
}
