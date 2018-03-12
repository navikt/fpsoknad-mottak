package no.nav.foreldrepenger.mottak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MottakApplication {

    private static final Logger LOG = LoggerFactory.getLogger(MottakApplication.class);

    public static void main(String[] args) {
        LOG.info("This thing is starting {}", System.getProperty("SPRING_ACTIVE_PROFILES"));
        SpringApplication.run(MottakApplication.class, args);
    }
}