package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.common.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG1;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG2;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.VEDLEGG3;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknad;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.MULTIPART_MIXED_VALUE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;

@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@AutoConfigureJsonTesters
@Disabled
@TestPropertySource(properties = { "sak.saker.url=http://sak",
        "token.x.well.known.url=http//www.localhost",
        "token.x.client.id=123",
        "token.x.private.jwk=123",
        "sak.securitytokenservice.password=pw",
        "sak.securitytokenservice.username=user",
        "sak.securitytokenservice.url=http://sts",
        "sts.uri=http://www.sts.no",
        "spring.cloud.vault.enabled=false",
        "sts.username=un", "sts.password=pw" })
@ComponentScan(basePackages = "no.nav.foreldrepenger.mottak")
class TestFPFordelSerialization {

    @MockBean
    SystemTokenTjeneste userService;

    @MockBean
    private Oppslag oppslag;
    @MockBean
    TokenUtil tokenUtil;

    @MockBean
    private ArbeidsforholdTjeneste arbeidsforhold;

    @Inject
    private KonvoluttGenerator konvoluttGenerator;

    @Inject
    @Qualifier(DELEGERENDE)
    private DomainMapper domainMapper;

    private static final AktørId AKTØRID = new AktørId("1111111111");
    private static final Fødselsnummer FNR = new Fødselsnummer("01010111111");
    private static final List<EnkeltArbeidsforhold> ARB_FORHOLD = arbeidsforhold();

    @BeforeEach
    void before() {
        when(oppslag.aktørId(eq(FNR))).thenReturn(AKTØRID);
        when(oppslag.fnr(eq(AKTØRID))).thenReturn(FNR);
        when(arbeidsforhold.hentArbeidsforhold()).thenReturn(ARB_FORHOLD);
    }

    @Test
    void testESFpFordel() {
        var engangstønad = engangssøknad(false, termin(), VEDLEGG3);
        assertNotNull(domainMapper.tilXML(engangstønad, AKTØRID, SøknadEgenskap.of(INITIELL_ENGANGSSTØNAD)));
    }

    @Test
    void testKonvolutt() {
        var søknad = foreldrepengesøknad( false, valgfrittVedlegg(ForeldrepengerTestUtils.ID142, LASTET_OPP));
        var konvolutt = konvoluttGenerator.generer(søknad, person(),
                SøknadEgenskap.of(INITIELL_FORELDREPENGER));
        assertNotNull(konvolutt.getMetadata());
        assertEquals(1, konvolutt.getVedlegg().size());
        assertMediaType(konvolutt.getPayload(), MULTIPART_MIXED_VALUE);
        assertEquals(søknad, konvolutt.getInnsending());
        assertNotNull(konvolutt.XMLHovedDokument());
        assertNotNull(konvolutt.PDFHovedDokument());
        assertTrue(konvolutt.erInitiellForeldrepenger());
    }

    @Test
    void testKonvoluttEndring() {
        var es = endringssøknad(ForeldrepengerTestUtils.VEDLEGG1, VEDLEGG2);
        var konvolutt = konvoluttGenerator.generer(es, person(),
                SøknadEgenskap.of(ENDRING_FORELDREPENGER));
        assertNotNull(konvolutt.getMetadata());
        assertNotNull(konvolutt.XMLHovedDokument());
        assertNotNull(konvolutt.PDFHovedDokument());
        assertEquals(es, konvolutt.getInnsending());
        assertEquals(2, konvolutt.getVedlegg().size());
        assertTrue(konvolutt.erEndring());
    }

    @Test
    void testKonvoluttEttersending() {
        var es = new Ettersending(Saksnummer.valueOf("42"), EttersendingsType.FORELDREPENGER, List.of(VEDLEGG1, VEDLEGG2), null);
        var konvolutt = konvoluttGenerator.generer(es,
                person(), SøknadEgenskap.ETTERSENDING_FORELDREPENGER);
        assertNotNull(konvolutt.getMetadata());
        assertEquals(2, konvolutt.getVedlegg().size());
        assertNull(konvolutt.XMLHovedDokument());
        assertNull(konvolutt.PDFHovedDokument());
        assertEquals(es, konvolutt.getInnsending());
        assertTrue(konvolutt.erEttersending());

    }

    private static List<EnkeltArbeidsforhold> arbeidsforhold() {
        return List.of(EnkeltArbeidsforhold.builder()
                .arbeidsgiverId("1234")
                .from(LocalDate.now().minusDays(200))
                .to(Optional.of(LocalDate.now()))
                .stillingsprosent(ProsentAndel.valueOf(90))
                .arbeidsgiverNavn("El Bedrifto").build());
    }

    private static void assertMediaType(HttpEntity<?> entity, String type) {
        assertEquals(type, entity.getHeaders().getFirst(CONTENT_TYPE));
    }
}
