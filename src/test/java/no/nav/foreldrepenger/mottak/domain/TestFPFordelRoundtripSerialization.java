package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ID142;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.innsending.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.innsending.SøknadDevController.INNSENDING_PREPROD;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.LOCAL;
import static no.nav.foreldrepenger.mottak.util.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.MottakApplicationLocal;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingHendelseProdusent;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.KonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.mottak.oppsla
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdConnection;g.STSSystemUserTokenService;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.security.token.support.test.JwtTokenGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { MottakApplicationLocal.class })
@ActiveProfiles(profiles = { LOCAL })
@TestPropertySource(properties = { "sts.uri=http://www.sts.no", "spring.cloud.vault.enabled=false",
        "spring.cloud.vault.token=00000",
        "kafka.username=un", "kafka.password=pw" })
@EnableConfigurationProperties
public class TestFPFordelRoundtripSerialization {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    FordelConnection connection;

    @MockBean
    STSSystemUserTokenService userService;

    @MockBean
    InnsendingHendelseProdusent publisher;
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
        assertEquals(new AktørId("42"), template.getForObject(INNSENDING_PREPROD + "/test", AktørId.class));
    }

    // @Test
    public void testFPSøknadSendV3() {
        Versjon versjon = V3;
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(versjon);
        Kvittering kvittering = sender.søk(søknad, TestUtils.person(),
                new SøknadEgenskap(versjon, INITIELL_FORELDREPENGER));
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

    @Test
    public void testESSøknadSendFPFordel() {
        Søknad engangssøknad = engangssøknad(Versjon.DEFAULT_VERSJON, false, fødsel(),
                norskForelder(Versjon.DEFAULT_VERSJON),
                påkrevdVedlegg(ID142));
        Kvittering kvittering = template.postForObject(INNSENDING + "/send", engangssøknad, Kvittering.class);
        assertEquals(IKKE_SENDT_FPSAK, kvittering.getLeveranseStatus());
    }

}
