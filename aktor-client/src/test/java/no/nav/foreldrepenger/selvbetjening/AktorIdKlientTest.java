package no.nav.foreldrepenger.selvbetjening;

import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.Fodselsnummer;
import no.nav.modig.testcertificates.TestCertificates;

public class AktorIdKlientTest {

    @Test
    @Ignore
    public void useSts() throws Exception {
       System.setProperty("no.nav.modig.security.sts.url", "https://xxxx.test.local/");
       System.setProperty("no.nav.modig.security.systemuser.username", "");
       System.setProperty("no.nav.modig.security.systemuser.password", "");
       TestCertificates.setupKeyAndTrustStore();
       AktorIdKlient theInstance = new AktorIdKlient();
       Optional<String> aktorId =
         theInstance.hentAktoerId(new Fodselsnummer("12345678910"));
       System.out.println(aktorId);
    }

}
