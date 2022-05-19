package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SEE_OTHER;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FordelKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.config.JacksonConfiguration;
import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.DelegerendePDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.EngangsstønadPdfGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengeInfoRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPdfGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivPdfEkstraktor;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.PdfElementRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGeneratorStub;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;
import no.nav.foreldrepenger.mottak.util.JacksonWrapper;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JacksonConfiguration.class)
class FPFordelTest {

    private static final AktørId AKTØRID = new AktørId("1111111111");
    private static final String FPFORDELURIBASE = "http://some.host.for.fpfordel";
    private static final String FPFORDELPOLLURISTRING = FPFORDELURIBASE + "/poll/id";
    private static final String FPINFOURISTRING = "http://some.host.for.fpinfo/status";

    private static final URI FPFORDELPOLLURI = URI.create(FPFORDELPOLLURISTRING);
    private static final URI POSTURI = URI.create(FPFORDELURIBASE + "/fpfordel/api/dokumentforsendelse");

    private static final String JOURNALID = "999";
    private static final String SAKSNR = "666";

    private static final List<EnkeltArbeidsforhold> ARB_FORHOLD = List.of(EnkeltArbeidsforhold.builder()
            .arbeidsgiverId("1234")
            .from(LocalDate.now().minusDays(200))
            .to(Optional.of(LocalDate.now()))
            .stillingsprosent(ProsentAndel.valueOf(90)).arbeidsgiverNavn("El Bedrifto")
            .build());

    @Mock
    private RestOperations restOperations;

    private final PdfGenerator pdfGenerator = new PdfGeneratorStub();

    @Autowired
    private ObjectMapper mapper;

    @Mock
    private Oppslag oppslag;
    @Mock
    private ArbeidsforholdTjeneste arbeidsforhold;
    @Mock
    private TokenUtil tokenHelper;
    private FordelConfig cfg;

    private ResponseEntity<FordelKvittering> pollReceipt202, pollReceipt200;
    private ResponseEntity<FordelKvittering> goysReceipt;
    private ResponseEntity<FordelKvittering> fordeltReceipt;

    private FordelSøknadSender sender;

    @BeforeEach
    void before() {
        cfg = new FordelConfig(URI.create(FPFORDELURIBASE));
        when(oppslag.aktørId(any(Fødselsnummer.class))).thenReturn(AKTØRID);
        when(arbeidsforhold.hentArbeidsforhold()).thenReturn(ARB_FORHOLD);
        pollReceipt202 = pollReceipt(HttpStatus.ACCEPTED);
        pollReceipt200 = pollReceipt(HttpStatus.OK);

        goysReceipt = gosysReceipt();
        fordeltReceipt = fordelt();
        when(restOperations.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class)))
                .thenReturn(pollReceipt202);
        sender = sender();
    }

    private FordelSøknadSender sender() {
        var mottakConfig = new MottakConfiguration();
        var jalla1 = new PdfElementRenderer();
        var jalla2 = new SøknadTextFormatter(mottakConfig.landkoder(),
                mottakConfig.kvitteringstekster());
        var jalla = new ForeldrepengeInfoRenderer(jalla1, jalla2);
        var infoskrivRenderer = new InfoskrivRenderer(jalla1, jalla2);
        var fp = new ForeldrepengerPdfGenerator(oppslag, arbeidsforhold, jalla,
                infoskrivRenderer);
        var es = new EngangsstønadPdfGenerator(jalla2, pdfGenerator, tokenHelper);
        var pdfGenerator = new DelegerendePDFGenerator(fp, es);
        var pdfSplitter = new InfoskrivPdfEkstraktor();

        var domainMapper = new DelegerendeDomainMapper(new V3ForeldrepengerDomainMapper(oppslag));
        var konvoluttGenerator = new KonvoluttGenerator(new MetdataGenerator(new JacksonWrapper(mapper)),
                domainMapper, pdfGenerator);
        return new FordelSøknadSender(
                new FordelConnection(restOperations, cfg,
                        new ResponseHandler(restOperations, 3)),
                konvoluttGenerator, pdfSplitter, new LoggingHendelseProdusent(), tokenHelper);
    }

    private static ResponseEntity<FordelKvittering> gosysReceipt() {
        return new ResponseEntity<>(new GosysKvittering(JOURNALID), OK);
    }

    private static ResponseEntity<FordelKvittering> pollReceipt(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, FPFORDELPOLLURISTRING);
        return new ResponseEntity<>(new PendingKvittering(Duration.ofMillis(100)), headers, status);
    }

    private static ResponseEntity<FordelKvittering> pollReceiptNoLocation() {
        return new ResponseEntity<>(new PendingKvittering(Duration.ofMillis(100)), ACCEPTED);
    }

    private static ResponseEntity<FordelKvittering> nullBody() {
        return new ResponseEntity<>(null, OK);
    }

    private static ResponseEntity<FordelKvittering> fpfordelPollReceiptError() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, FPFORDELPOLLURISTRING);
        return new ResponseEntity<>(new PendingKvittering(Duration.ofMillis(100)), headers, BAD_REQUEST);
    }

    private static ResponseEntity<FordelKvittering> fordelt() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, FPINFOURISTRING);
        return new ResponseEntity<>(new FPSakFordeltKvittering(JOURNALID, SAKSNR), headers, SEE_OTHER);
    }

    @Test
    void pollTwiceThenGosys() {
        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                goysReceipt);
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(), person(),
               SøknadEgenskap.of(SøknadType.INITIELL_FORELDREPENGER));
        assertNull(kvittering.getSaksNr());
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
        verify(restOperations, times(2)).getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class));
    }

    @Test
    void poll3GivesUp() {
        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                pollReceipt200, pollReceipt200);
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(), person(),
                SøknadEgenskap.of(SøknadType.INITIELL_FORELDREPENGER));
        assertNull(kvittering.getSaksNr());
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
        verify(restOperations, times(3)).getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class));
    }

    @Test
    void pollOnceThenOK() {
        var value = new Kvittering(null, "42", null, null);

        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(), person(),
                SøknadEgenskap.of(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getSaksNr(), SAKSNR);
    }

    @Test
    void pollNoLocation() {
        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt202,
                pollReceiptNoLocation());
        sender.søk(foreldrepengeSøknad(), person(), SøknadEgenskap.of(SøknadType.INITIELL_FORELDREPENGER));
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
    }

    @Test
    void unexpectedStatusCode() {
        when(restOperations.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class)))
                .thenReturn(fpfordelPollReceiptError());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(), person(),
                SøknadEgenskap.of(SøknadType.INITIELL_FORELDREPENGER));
        assertNull(kvittering.getSaksNr());
        verify(restOperations, never()).getForEntity(eq(POSTURI), eq(FordelKvittering.class));
    }

    @Test
    void testNullBody() {
        when(restOperations.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class)))
                .thenReturn(nullBody());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(), person(),
                SøknadEgenskap.of(SøknadType.INITIELL_FORELDREPENGER));
        assertNull(kvittering.getSaksNr());
        verify(restOperations, never()).getForEntity(eq(POSTURI), eq(FordelKvittering.class));
    }
}
