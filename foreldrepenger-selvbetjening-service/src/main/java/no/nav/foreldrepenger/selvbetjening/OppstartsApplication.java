package no.nav.foreldrepenger.selvbetjening;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import no.nav.modig.testcertificates.TestCertificates;

@SpringBootApplication
@ComponentScan("no.nav.foreldrepenger")
public class OppstartsApplication {
	static {
		TestCertificates.setupKeyAndTrustStore();
	}

	public static void main(String[] args) {
		System.setProperty("no.nav.modig.security.sts.url", System.getProperty("SECURITYTOKENSERVICE_URL"));
		System.setProperty("no.nav.modig.security.systemuser.username", System.getProperty("FPSELVBETJENING_USERNAME"));
		System.setProperty("no.nav.modig.security.systemuser.password", System.getProperty("FPSELVBETJENING_PASSWORD"));
		SpringApplication.run(OppstartsApplication.class, args);
	}

	@Bean
	public Module jodaModule() {
		return new JodaModule();
	}
}
