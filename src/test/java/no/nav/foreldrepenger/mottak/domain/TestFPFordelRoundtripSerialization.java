package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.nesteMåned;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID142;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.http.controllers.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.http.controllers.SøknadPreprodController.INNSENDING_PREPROD;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static org.eclipse.jetty.http.HttpStatus.UNPROCESSABLE_ENTITY_422;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.MottakApplicationLocal;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.innsending.DualSøknadSender;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.VersjonsBevisstDomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.VersjonsBevisstXMLMapper;
import no.nav.foreldrepenger.mottak.util.JAXBESV1Helper;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.security.oidc.test.support.JwtTokenGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { MottakApplicationLocal.class })
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = { DEV, PREPROD })
public class TestFPFordelRoundtripSerialization {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    FPFordelConnection connection;

    @Autowired
    ObjectMapper mapper;

    private static final JAXBESV1Helper JAXB_ES = new JAXBESV1Helper();

    @Autowired
    CallIdGenerator refGenerator;
    @Autowired
    VersjonsBevisstDomainMapper søknadXMLGenerator;
    @Autowired
    VersjonsBevisstXMLMapper xmlMapper;
    @Autowired
    FPFordelKonvoluttGenerator konvoluttGenerator;
    @Autowired
    DualSøknadSender sender;

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

    @Test
    public void testFPForeldrepengerSøknadXMLV1() {
        Versjon versjon = V1;
        Søknad original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        assertEquals(original,
                xmlMapper
                        .tilSøknad(template.postForObject(INNSENDING_PREPROD + "/søknad", original, String.class,
                                versjon)));
    }

    @Test
    public void testFPForeldrepengerSøknadXMLV2() {
        Versjon versjon = V2;
        Søknad original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        assertEquals(original, xmlMapper
                .tilSøknad(template.postForObject(INNSENDING_PREPROD + "/søknadV2", original, String.class, versjon)));
    }

    @Test
    public void testFPSøknadSendV1() {
        Versjon versjon = V1;
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        Kvittering kvittering = sender.send(søknad, TestUtils.person(), versjon);
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    public void testFPSøknadSendV2() {
        Versjon versjon = V2;
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        Kvittering kvittering = sender.send(søknad, TestUtils.person(), versjon);
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    public void testESSøknadFødselMedNorskFar() {
        Versjon versjon = V1;
        Søknad engangssøknad = engangssøknad(versjon, false, fødsel(), norskForelder(versjon),
                påkrevdVedlegg(ID142));
        SoeknadsskjemaEngangsstoenad response = JAXB_ES.unmarshal(
                template.postForObject(INNSENDING_PREPROD + "/søknadES", engangssøknad, String.class),
                SoeknadsskjemaEngangsstoenad.class);
        assertEquals(engangssøknad.getBegrunnelseForSenSøknad(), response.getOpplysningerOmBarn().getBegrunnelse());
    }

    @Test
    public void testESSøknadSend() {
        Versjon versjon = V1;
        Søknad engangssøknad = engangssøknad(versjon, false, fødsel(), norskForelder(versjon),
                påkrevdVedlegg(ID142));
        Kvittering kvittering = sender.send(engangssøknad, TestUtils.person());
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    public void testSøknadFødselFramtidShouldNotValidate() {
        Versjon versjon = V1;
        assertEquals(UNPROCESSABLE_ENTITY_422, template.postForEntity(INNSENDING_PREPROD + "/søknad",
                engangssøknad(versjon, false, fødsel(nesteMåned()), norskForelder(versjon),
                        påkrevdVedlegg(ID142)),
                String.class).getStatusCodeValue());
    }
}
