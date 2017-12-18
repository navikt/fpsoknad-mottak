package no.nav.foreldrepenger.selvbetjening;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.modig.testcertificates.TestCertificates;

@SpringBootApplication
public class  OppstartsApplication {
	

  public static void main(String[] args) {
	  TestCertificates.setupKeyAndTrustStore();
	  System.setProperty("no.nav.modig.security.sts.url",System.getenv("SECURITYTOKENSERVICE_URL")); 	 
      System.setProperty("no.nav.modig.security.systemuser.username",System.getenv("FPSELVBETJENING_USERNAME")); 	 
      System.setProperty("no.nav.modig.security.systemuser.password",System.getenv("FPSELVBETJENING_PASSWORD")); 	 
      SpringApplication.run(OppstartsApplication.class, args);
  }
}
