package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.LOCAL;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID142;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.innsending.MottakController.INNSENDING;
import static no.nav.foreldrepenger.mottak.innsending.MottakDevController.INNSENDING_PREPROD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.MottakApplicationLocal;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingHendelseProdusent;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.KonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemToken;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.test.JwtTokenGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { MottakApplicationLocal.class })
@ActiveProfiles(profiles = { LOCAL })
@TestPropertySource(properties = {
    "fpfordel.enabled=false",
    "sts.uri=http://www.sts.no",
    "spring.cloud.vault.enabled=false",
    "spring.cloud.vault.token=00000",
    "sts.username=un",
    "sts.password=pw",
    "securitytokenservice.username=un",
    "securitytokenservice.password=pw",
    "securitytokenservice.url",
    "aareg.rs.url=test",
    "organisasjon.v4.url=test",
    "oidc.sts.issuer.url=test",
    "oidc.sts.token.path=test",
    "kafka.username=vtp",
    "kafka.password=vtp",
    "sak.rs.url=test",
    "loginservice.idporten.discovery.url=test",
    "loginservice.idporten.audience=test",
    "bootstrap.servers=test",
    "pdl.graphql.base.url=test",
    "fpsoknad.mottak=test",
    "oppslag.url=test",
    "fpfordel.base.url=test",
    "dkif.base.url=test",
    "fpinfo.base.url=test"
    })


@EnableConfigurationProperties
public class TestFPFordelRoundtripSerialization {

    private static MockOAuth2Server SERVER;

    @Autowired
    private TestRestTemplate template;

    @MockBean
    SystemTokenTjeneste userService;
    // @Mock
    // SystemToken token;
    @MockBean
    InnsendingHendelseProdusent publisher;

    @MockBean
    ArbeidsforholdTjeneste arbeidsforhold;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    CallIdGenerator refGenerator;
    @Autowired
    @Qualifier(DELEGERENDE)
    DomainMapper søknadXMLGenerator;
    @Autowired
    @Qualifier(DELEGERENDE)
    XMLSøknadMapper xmlMapper;
    @Autowired
    KonvoluttGenerator konvoluttGenerator;

    @Autowired
    SøknadSender sender;

    @BeforeAll
    public static void startup() throws IOException {
        SERVER = new MockOAuth2Server();
        SERVER.start();
    }

    @AfterAll
    public static void shutdown() throws IOException {
        SERVER.shutdown();
    }

    @BeforeEach
    public void setAuthoriztion() {
        var token = SERVER.issueToken();
        var t = new JwtToken(token.serialize().toString());
        when(userService.getSystemToken()).thenReturn(new SystemToken(t, null, null, null));
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
        assertEquals(new AktørId("42"), template.getForObject(INNSENDING_PREPROD + "/test", AktørId.class));
    }

    @Test
    public void testFPSøknadSendV3() {
        Versjon versjon = V3;
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        Kvittering kvittering = sender.søk(søknad, TestUtils.person(),
                new SøknadEgenskap(versjon, INITIELL_FORELDREPENGER));
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    public void testESSøknadSendFPFordel() {
        Søknad engangssøknad = engangssøknad(false, fødsel(),
                norskForelder(),
                påkrevdVedlegg(ID142));
        var kvittering = template.postForObject(INNSENDING + "/send",
                engangssøknad, Kvittering.class);
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

}
