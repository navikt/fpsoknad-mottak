package no.nav.foreldrepenger.mottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication
public class MottakApplication {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplication.class, args);
        // context.getBean(JmsTemplate.class).send("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", textMessage("hello
        // world"));
        // ("QA.T1_DOKMOT.MOTTA_FORSENDELSE_DITT_NAV", "hei");
    }

    @Bean("landkoder")
    public MessageSource landkoder() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("landkoder");
        return messageSource;
    }

    @Bean("kvitteringstekster")
    public MessageSource kvitteringstekster() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("kvitteringstekster");
        return messageSource;
    }

}
