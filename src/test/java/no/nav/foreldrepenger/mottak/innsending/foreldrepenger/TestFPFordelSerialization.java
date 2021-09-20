package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.domain.felles.EttersendingsType.foreldrepenger;
import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.ForeldrepengerTestUtils.V2;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.ForeldrepengerTestUtils.V3;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.ForeldrepengerTestUtils.VEDLEGG1;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.ForeldrepengerTestUtils.søknad;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.common.innsending.SøknadType.ENDRING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.util.Versjon.DEFAULT_VERSJON;
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
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.common.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.common.util.Versjon;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.oppslag.sts.SystemTokenTjeneste;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

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
        "varsel.uri=mq:80/test",
        "sts.uri=http://www.sts.no",
        "spring.cloud.vault.enabled=false",
        "sts.username=un", "sts.password=pw" })
@ComponentScan(basePackages = "no.nav.foreldrepenger.mottak")
class TestFPFordelSerialization {

    private static final Inspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    @MockBean
    SystemTokenTjeneste userService;

    @MockBean
    private Oppslag oppslag;
    @MockBean
    TokenUtil tokenUtil;

    @MockBean
    private ArbeidsforholdTjeneste arbeidsforhold;
    @MockBean
    private InnsendingHendelseProdusent publisher;

    @Inject
    private KonvoluttGenerator konvoluttGenerator;

    @Inject
    @Qualifier(DELEGERENDE)
    private XMLSøknadMapper xmlMapper;
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
        when(arbeidsforhold.hentAktiveArbeidsforhold()).thenReturn(ARB_FORHOLD);
    }

    @Test
    void testEndringssøknadRoundtrip() {
        testEndringssøknadRoundtrip(DEFAULT_VERSJON);
    }

    @Test
    void testESFpFordel() {
        var engangstønad = engangssøknad(false, termin(), norskForelder(), V3);
        assertNotNull(domainMapper.tilXML(engangstønad, AKTØRID, SøknadEgenskap.of(INITIELL_ENGANGSSTØNAD)));
    }

    @Test
    void testSøknadRoundtrip() {
        var original = søknadMedEttOpplastetEttIkkeOpplastetVedlegg(DEFAULT_VERSJON);
        String xml = domainMapper.tilXML(original, AKTØRID, new SøknadEgenskap(DEFAULT_VERSJON, INITIELL_FORELDREPENGER));
        var egenskap = INSPEKTØR.inspiser(xml);
        assertEquals(DEFAULT_VERSJON, egenskap.getVersjon());
        assertEquals(egenskap.getType(), INITIELL_FORELDREPENGER);
        assertEquals(original, xmlMapper.tilSøknad(xml, egenskap));
    }

    @Test
    void testKonvolutt() {
        var søknad = søknad(DEFAULT_VERSJON, false, valgfrittVedlegg(ForeldrepengerTestUtils.ID142, LASTET_OPP));
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
        var es = endringssøknad(DEFAULT_VERSJON, ForeldrepengerTestUtils.VEDLEGG1, ForeldrepengerTestUtils.V2);
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
        var es = new Ettersending(foreldrepenger, "42", VEDLEGG1, V2);
        var konvolutt = konvoluttGenerator.generer(es,
                person(), SøknadEgenskap.ETTERSENDING_FORELDREPENGER);
        assertNotNull(konvolutt.getMetadata());
        assertEquals(2, konvolutt.getVedlegg().size());
        assertNull(konvolutt.XMLHovedDokument());
        assertNull(konvolutt.PDFHovedDokument());
        assertEquals(es, konvolutt.getInnsending());
        assertTrue(konvolutt.erEttersending());

    }

    public void testEndringssøknadRoundtrip(Versjon v) {
        var original = endringssøknad(v, VEDLEGG1, V2);
        String xml = domainMapper.tilXML(original, AKTØRID, new SøknadEgenskap(v, ENDRING_FORELDREPENGER));
        var egenskap = INSPEKTØR.inspiser(xml);
        assertEquals(v, egenskap.getVersjon());
        assertEquals(ENDRING_FORELDREPENGER, egenskap.getType());
        Endringssøknad respons = Endringssøknad.class.cast(xmlMapper.tilSøknad(xml, egenskap));
        var originalFordeling = no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger.class
                .cast(original.getYtelse()).getFordeling();
        assertEquals(originalFordeling, no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger.class
                .cast(respons.getYtelse()).getFordeling());
        assertEquals(original.getSaksnr(), respons.getSaksnr());
    }

    private static List<EnkeltArbeidsforhold> arbeidsforhold() {
        return List.of(EnkeltArbeidsforhold.builder()
                .arbeidsgiverId("1234")
                .from(LocalDate.now().minusDays(200))
                .to(Optional.of(LocalDate.now()))
                .stillingsprosent(new ProsentAndel(90))
                .arbeidsgiverNavn("El Bedrifto").build());
    }

    private static void assertMediaType(HttpEntity<?> entity, String type) {
        assertEquals(type, entity.getHeaders().getFirst(CONTENT_TYPE));
    }
}
