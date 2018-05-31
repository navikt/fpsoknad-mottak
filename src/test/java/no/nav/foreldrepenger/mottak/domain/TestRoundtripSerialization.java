package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.nesteMåned;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.http.DokmotMottakController.DOKMOT;
import static no.nav.foreldrepenger.mottak.http.DokmotMottakPreprodController.DOKMOT_PREPROD;
import static no.nav.foreldrepenger.mottak.util.Jaxb.context;
import static no.nav.security.spring.oidc.test.JwtTokenGenerator.createSignedJWT;
import static org.eclipse.jetty.http.HttpStatus.UNPROCESSABLE_ENTITY_422;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.MottakApplicationLocal;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelConnection;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSøknadGenerator;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = MottakApplicationLocal.class)
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "preprod,dev")
@AutoConfigureWireMock(port = 0)
public class TestRoundtripSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestRoundtripSerialization.class);

    @Autowired
    private TestRestTemplate template;

    @Autowired
    FPFordelConnection connection;

    @Autowired
    UUIDIdGenerator refGenerator;
    @Autowired
    FPFordelSøknadGenerator søknadXMLGenerator;
    @Autowired
    FPFordelKonvoluttGenerator konvoluttGenerator;

    @Before
    public void setAuthoriztion() {
        template.getRestTemplate().setInterceptors(Collections.singletonList((request, body,
                execution) -> {
            request.getHeaders().add(AUTHORIZATION, "Bearer " +
                    createSignedJWT("12345678910").serialize());
            return execution.execute(request, body);
        }));
    }

    @Test
    public void testPing() {
        assertEquals("Hallo joe fra ubeskyttet ressurs",
                template.getForObject(DOKMOT + "/ping?navn=joe", String.class));
    }

    @Test
    public void testPing1() {
        assertEquals("Hallo joe fra beskyttet ressurs",
                template.getForObject(DOKMOT + "/ping1?navn=joe", String.class));
    }

    @Test
    public void testSøknadFødselMedNorskFar() throws IOException {

        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg());
        SoeknadsskjemaEngangsstoenad response = unmarshal(
                template.postForObject(DOKMOT_PREPROD + "/søknad", engangssøknad,
                        String.class),
                SoeknadsskjemaEngangsstoenad.class);
        assertEquals(engangssøknad.getBegrunnelseForSenSøknad(), response.getOpplysningerOmBarn().getBegrunnelse());

    }

    @Test
    public void testSøknadSend() throws IOException {
        assertEquals("0",
                template.postForObject(DOKMOT + "/send",
                        engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg()), Kvittering.class)
                        .getReferanseId());
    }

    @Test
    public void testSøknadFødselFramtidShouldNotValidate() throws IOException {
        assertEquals(UNPROCESSABLE_ENTITY_422, template.postForEntity(DOKMOT_PREPROD + "/søknad",
                engangssøknad(false, fødsel(nesteMåned()), norskForelder(), påkrevdVedlegg()),
                String.class).getStatusCodeValue());

    }

    @Test
    public void testSøknadKonvoluttFødselMedNorskFar() throws IOException {
        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg());
        Dokumentforsendelse response = unmarshal(
                template.postForObject(DOKMOT_PREPROD + "/konvolutt", engangssøknad, String.class),
                Dokumentforsendelse.class);
        assertEquals("FOR", response.getForsendelsesinformasjon().getTema().getValue());

    }

    private static <T> T unmarshal(String xml, Class<T> clazz) {
        LOG.info("Mottok xml\n{}", xml);
        return Jaxb.unmarshal(xml, context(clazz), clazz);
    }
}
