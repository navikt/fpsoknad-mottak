package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.nesteMåned;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID142;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.innsending.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.innsending.SøknadPreprodController.INNSENDING_PREPROD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsending.mappers.Mappable.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static org.eclipse.jetty.http.HttpStatus.UNPROCESSABLE_ENTITY_422;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.MottakApplicationLocal;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLMapper;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.mottak.util.jaxb.ESV1JAXBUtil;
import no.nav.security.oidc.test.support.JwtTokenGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { MottakApplicationLocal.class }, properties = {
        "engangstønad.destinasjon=DOKMOT" })
@ActiveProfiles(profiles = { DEV, PREPROD })
public class TestFPFordelRoundtripSerialization {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    FPFordelConnection connection;

    @Autowired
    ObjectMapper mapper;

    private static final ESV1JAXBUtil JAXB = new ESV1JAXBUtil();

    @Autowired
    CallIdGenerator refGenerator;
    @Autowired
    @Qualifier(DELEGERENDE)
    DomainMapper søknadXMLGenerator;
    @Autowired
    @Qualifier(DELEGERENDE)
    XMLMapper xmlMapper;
    @Autowired
    FPFordelKonvoluttGenerator konvoluttGenerator;

    @Autowired
    SøknadSender sender;

    @BeforeEach
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
    public void test1() {
        assertEquals(new AktorId("42"), template.getForObject(INNSENDING_PREPROD + "/test", AktorId.class));
    }

    @Test
    public void testFPForeldrepengerSøknadXMLV1() {
        Versjon versjon = V1;
        Søknad original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        String xml = template.postForObject(INNSENDING_PREPROD + "/søknad", original, String.class);
        SøknadEgenskap egenskaper = new XMLStreamSøknadInspektør().inspiser(xml);
        assertEquals(original, xmlMapper.tilSøknad(xml, egenskaper));
    }

    @Test
    public void testFPForeldrepengerSøknadXMLV2() {
        Versjon versjon = V2;
        Søknad original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        String xml = template.postForObject(INNSENDING_PREPROD + "/søknadV2", original, String.class);
        SøknadEgenskap egenskaper = new XMLStreamSøknadInspektør().inspiser(xml);
        assertEquals(original, xmlMapper.tilSøknad(xml, egenskaper));
    }

    @Test
    public void testFPSøknadSendV1() {
        Versjon versjon = V1;
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        Kvittering kvittering = sender.send(søknad, TestUtils.person(),
                new SøknadEgenskap(versjon, INITIELL_FORELDREPENGER));
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    public void testFPSøknadSendV2() {
        Versjon versjon = V2;
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        Kvittering kvittering = sender.send(søknad, TestUtils.person(),
                new SøknadEgenskap(versjon, INITIELL_FORELDREPENGER));
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    public void testESSøknadSendFPFordel() {
        Søknad engangssøknad = engangssøknad(V2, false, fødsel(), norskForelder(V2),
                påkrevdVedlegg(ID142));
        Kvittering kvittering = template.postForObject(INNSENDING + "/send", engangssøknad, Kvittering.class);
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
