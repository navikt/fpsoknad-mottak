package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.mottak.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.util.Jaxb.context;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = MottakApplication.class)
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "preprod,dev")
public class TestRoundtripSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestRoundtripSerialization.class);

    @Autowired
    private TestRestTemplate template;

    @Test
    public void testPing() {
        assertEquals("Hello joe", template.getForObject("/mottak/dokmot/ping?navn=joe", String.class));
    }

    @Test
    public void testSøknadFødselMedNorskFar() throws IOException {
        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg());
        SoeknadsskjemaEngangsstoenad response = unmarshal(
                template.postForObject("/mottak/preprod/søknad", engangssøknad,
                        String.class),
                SoeknadsskjemaEngangsstoenad.class);
        assertEquals(engangssøknad.getBegrunnelseForSenSøknad(), response.getOpplysningerOmBarn().getBegrunnelse());
        // TODO more checks
    }

    @Test
    public void testSøknadFødselFramtidShouldNotValidate() throws IOException {
        Søknad engangssøknad = engangssøknad(false, fødsel(TestUtils.nesteMåned()), norskForelder(), påkrevdVedlegg());
        ResponseEntity<String> response = template.postForEntity("/mottak/preprod/søknad", engangssøknad, String.class);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, response.getStatusCodeValue());
    }

    @Test
    public void testSøknadKonvoluttFødselMedNorskFar() throws IOException {
        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg());
        Dokumentforsendelse response = unmarshal(
                template.postForObject("/mottak/preprod/konvolutt", engangssøknad,
                        String.class),
                Dokumentforsendelse.class);
        assertEquals("FOR", response.getForsendelsesinformasjon().getTema().getValue());
        // TODO more tests

    }

    private static <T> T unmarshal(String xml, Class<T> clazz) {
        LOG.info("Mottok xml\n{}", xml);
        return Jaxb.unmarshal(xml, context(clazz), clazz);
    }
}
