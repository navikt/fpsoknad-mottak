package no.nav.foreldrepenger.selvbetjening;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.modig.testcertificates.TestCertificates;

@SpringBootApplication
public class  OppstartsApplication {

  public static void main(String[] args) {
	  TestCertificates.setupKeyAndTrustStore();
	  System.setProperty("no.nav.modig.security.sts.url",System.getProperty("SECURITYTOKENSERVICE_URL")); 	 
      System.setProperty("no.nav.modig.security.systemuser.username",System.getProperty("FPSELVBETJENING_USERNAME")); 	 
      System.setProperty("no.nav.modig.security.systemuser.password",System.getProperty("FPSELVBETJENING_PASSWORD")); 	 
      SpringApplication.run(OppstartsApplication.class, args);
  }
}
