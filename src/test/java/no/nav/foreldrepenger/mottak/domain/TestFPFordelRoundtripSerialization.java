package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.LOCAL;
import static no.nav.foreldrepenger.boot.conditionals.EnvUtil.TEST;
import static no.nav.foreldrepenger.common.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.util.Versjon.V3;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID142;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.innsending.MottakController.INNSENDING;
import static no.nav.foreldrepenger.mottak.innsending.MottakDevController.INNSENDING_PREPROD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.CallIdGenerator;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.http.WebClientConfiguration.TokenXExchangeFilterFunction;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingHendelseProdusent;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.KonvoluttGenerator;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsInfo;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemToken;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.TokenUtil;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { MottakApplicationLocal.class })
@Disabled
@ActiveProfiles(profiles = { LOCAL, TEST })
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
        "organisasjon.rs.url=test",
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
        "token.x.well.known.url=http//www.localhost",
        "token.x.well.client.id=123",
        "token.x.well.private.jwk=123",

        "fpinfo.base.url=test"
})

@EnableConfigurationProperties
@EnableJwtTokenValidation
class TestFPFordelRoundtripSerialization {

    private static MockOAuth2Server SERVER;

    @Autowired
    private TestRestTemplate template;

    @MockBean
    SystemTokenTjeneste userService;
    @MockBean
    InnsendingHendelseProdusent publisher;
    @MockBean
    ArbeidsInfo arbeidsforhold;

    @MockBean
    TokenUtil tokenUtil;
    @MockBean
    TokenXExchangeFilterFunction tokenX;
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
    static void startup() throws IOException {
        SERVER = new MockOAuth2Server();
        SERVER.start();
    }

    @AfterAll
    static void shutdown() throws IOException {
        SERVER.shutdown();
    }

    @BeforeEach
    void setAuthoriztion() {
        var t = new JwtToken(SERVER.issueToken().serialize().toString());
        when(userService.getSystemToken()).thenReturn(new SystemToken(t, null, null, null));
        template.getRestTemplate().setInterceptors(Collections.singletonList((request, body,
                execution) -> {
            request.getHeaders().add(AUTHORIZATION, "Bearer " +
                    SERVER.issueToken("12345678910").serialize());
            return execution.execute(request, body);
        }));
    }

    @Test
    void testPing() {
        assertEquals("Hallo joe fra ubeskyttet ressurs",
                template.getForObject(INNSENDING + "/ping?navn=joe", String.class));
    }

    @Test
    void test1() {
        assertEquals(new AktørId("42"), template.getForObject(INNSENDING_PREPROD + "/test", AktørId.class));
    }

    @Test
    void testFPSøknadSendV3() {
        var versjon = V3;
        var søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        var kvittering = sender.søk(søknad, TestUtils.person(),
                new SøknadEgenskap(versjon, INITIELL_FORELDREPENGER));
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    void testESSøknadSendFPFordel() {
        var engangssøknad = engangssøknad(false, fødsel(),
                norskForelder(),
                påkrevdVedlegg(ID142));
        var kvittering = template.postForObject(INNSENDING + "/send",
                engangssøknad, Kvittering.class);
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

}
