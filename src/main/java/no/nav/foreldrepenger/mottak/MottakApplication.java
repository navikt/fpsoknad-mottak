package no.nav.foreldrepenger.mottak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import no.nav.modig.testcertificates.TestCertificates;

@SpringBootApplication
@ComponentScan("no.nav.foreldrepenger")
public class MottakApplication {

    public static void main(String[] args) {
        TestCertificates.setupKeyAndTrustStore();
        SpringApplication.run(MottakApplication.class, args);
    }
}
