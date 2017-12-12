package no.nav.foreldrepenger.selvbetjening;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.Fodselsnummer;
import no.nav.modig.testcertificates.TestCertificates;

	
	@Ignore
	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class AktorIdKlientTest {


	       

	        @Inject
	        private AktorIdKlient client;

	     @Test
	    public void useSts() throws Exception {
	          TestCertificates.setupKeyAndTrustStore();
	       Optional<AktorId> aktorId = client.aktorIdForFnr(new Fodselsnummer("06055301296"));
	       System.out.println(aktorId);
	    }
	    
	 }
	    


