package no.nav.foreldrepenger.mottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("no.nav.foreldrepenger")
public class MottakApplication {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplication.class, args);
    }
}
