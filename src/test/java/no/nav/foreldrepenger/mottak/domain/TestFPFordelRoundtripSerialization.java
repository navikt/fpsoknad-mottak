package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.nesteMåned;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.http.controllers.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.http.controllers.SøknadPreprodController.INNSENDING_PREPROD;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.MottakApplicationLocal;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.ForeldrepengerSøknadMapper;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.foreldrepenger.mottak.util.Jaxb.ValidationMode;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.security.oidc.test.support.JwtTokenGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { MottakApplicationLocal.class })
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "preprod,dev")
public class TestFPFordelRoundtripSerialization {

    private static final Logger LOG = LoggerFactory.getLogger(TestFPFordelRoundtripSerialization.class);

    @Autowired
    private TestRestTemplate template;

    @Autowired
    FPFordelConnection connection;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CallIdGenerator refGenerator;
    @Autowired
    ForeldrepengerSøknadMapper søknadXMLGenerator;

    @Autowired
    FPFordelKonvoluttGenerator konvoluttGenerator;

    @Before
    public void setAuthoriztion() {

        template.getRestTemplate().setInterceptors(Collections.singletonList((request, body,
                execution) -> {
            request.getHeaders().add(AUTHORIZATION, "Bearer " +
                    JwtTokenGenerator.createSignedJWT("12345678910").serialize());
            return execution.execute(request, body);
        }));
    }

    @Test
    public void testPing() {
        assertEquals("Hallo joe fra ubeskyttet ressurs",
                template.getForObject(INNSENDING + "/ping?navn=joe", String.class));
    }

    // @Test
    public void testForeldrepengerSøknadXML() throws IOException {
        Søknad foreldrepenger = ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg();
        String xml = template.postForObject(INNSENDING_PREPROD + "/søknad", foreldrepenger, String.class);
        Søknad søknad = søknadXMLGenerator.tilSøknad(xml);
        assertEquals(foreldrepenger.getMottattdato().toLocalDate(), søknad.getMottattdato().toLocalDate());
        assertEquals(foreldrepenger.getBegrunnelseForSenSøknad(), søknad.getBegrunnelseForSenSøknad());
    }

    @Test
    public void testForeldrepengerSøknadSend() throws IOException {

        assertEquals(LeveranseStatus.IKKE_SENDT_FPSAK,
                template.postForObject(INNSENDING + "/send",
                        ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg(), Kvittering.class)
                        .getLeveranseStatus());
    }

    @Test
    public void testSøknadFødselMedNorskFar() throws IOException {

        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(),
                påkrevdVedlegg(ForeldrepengerTestUtils.ID142));
        SoeknadsskjemaEngangsstoenad response = unmarshal(
                template.postForObject(INNSENDING_PREPROD + "/søknad", engangssøknad, String.class),
                SoeknadsskjemaEngangsstoenad.class, ValidationMode.ENGANGSSTØNAD);
        assertEquals(engangssøknad.getBegrunnelseForSenSøknad(), response.getOpplysningerOmBarn().getBegrunnelse());

    }

    @Test
    public void testEngangsstønadSøknadSend() throws IOException {
        assertEquals(LeveranseStatus.IKKE_SENDT_FPSAK, template.postForObject(INNSENDING + "/send",
                engangssøknad(false, fødsel(), norskForelder(), påkrevdVedlegg(ForeldrepengerTestUtils.ID142)),
                Kvittering.class)
                .getLeveranseStatus());
    }

    @Test
    public void testSøknadFødselFramtidShouldNotValidate() throws IOException {
        assertEquals(UNPROCESSABLE_ENTITY_422, template.postForEntity(INNSENDING_PREPROD + "/søknad",
                engangssøknad(false, fødsel(nesteMåned()), norskForelder(),
                        påkrevdVedlegg(ForeldrepengerTestUtils.ID142)),
                String.class).getStatusCodeValue());

    }

    @Test
    public void testSøknadKonvoluttFødselMedNorskFar() throws IOException {
        Søknad engangssøknad = engangssøknad(false, fødsel(), norskForelder(),
                påkrevdVedlegg(ForeldrepengerTestUtils.ID142));
        Dokumentforsendelse response = unmarshal(
                template.postForObject(INNSENDING_PREPROD + "/konvolutt", engangssøknad, String.class),
                Dokumentforsendelse.class, ValidationMode.ENGANGSSTØNAD);
        assertEquals("FOR", response.getForsendelsesinformasjon().getTema().getValue());

    }

    private static <T> T unmarshal(String xml, Class<T> clazz, ValidationMode mode) {
        LOG.info("Mottok xml\n{}", xml);
        return Jaxb.unmarshal(xml, clazz, mode);
    }
}
