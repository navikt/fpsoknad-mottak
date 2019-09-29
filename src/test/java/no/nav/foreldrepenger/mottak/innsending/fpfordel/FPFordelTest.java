package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepengeSøknad;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelConfig;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelConnection;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.KonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.MetdataGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.ResponseHandler;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FordelSøknadSender;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.LoggingHendelseProdusent;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.DelegerendePDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.EngangsstønadPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengeInfoRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengerPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.PDFElementRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivPdfExtractor;
import no.nav.foreldrepenger.mottak.innsyn.FPInfoSaksPoller;
import no.nav.foreldrepenger.mottak.innsyn.ForsendelseStatus;
import no.nav.foreldrepenger.mottak.innsyn.ForsendelsesStatusKvittering;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
public class FPFordelTest {

    private static final AktørId AKTØRID = new AktørId("1111111111");
    private static final String FPFORDELURIBASE = "http://some.host.for.fpfordel";
    private static final String FPFORDELPOLLURISTRING = FPFORDELURIBASE + "/poll/id";
    private static final String FPINFOURISTRING = "http://some.host.for.fpinfo/status";

    private static final URI FPFORDELPOLLURI = URI.create(FPFORDELPOLLURISTRING);
    private static final URI POSTURI = URI.create(FPFORDELURIBASE + "/fpfordel/api/dokumentforsendelse");

    private static final URI FPINFOURI = URI.create(FPINFOURISTRING);

    private static final String JOURNALID = "999";
    private static final String SAKSNR = "666";

    private static final List<Arbeidsforhold> ARB_FORHOLD = Arrays
            .asList(new Arbeidsforhold("1234", "", LocalDate.now().minusDays(200),
                    Optional.of(LocalDate.now()), new ProsentAndel(90), "El Bedrifto"));

    @Mock
    private RestOperations restOperations;

    private MeterRegistry registry = new SimpleMeterRegistry();
    @Mock
    private Oppslag oppslag;
    @Mock
    private FPInfoSaksPoller poller;
    @Mock
    private TokenUtil tokenHelper;
    private FordelConfig cfg;

    private ResponseEntity<FordelKvittering> pollReceipt202, pollReceipt200;
    private ResponseEntity<FordelKvittering> goysReceipt;
    private ResponseEntity<FordelKvittering> fordeltReceipt;

    private FordelSøknadSender sender;

    @BeforeEach
    public void before() {
        cfg = new FordelConfig();
        cfg.setEnabled(true);
        cfg.setUri(URI.create(FPFORDELURIBASE));

        when(oppslag.getAktørId(any(Fødselsnummer.class))).thenReturn(AKTØRID);
        when(oppslag.getArbeidsforhold()).thenReturn(ARB_FORHOLD);
        pollReceipt202 = pollReceipt(HttpStatus.ACCEPTED);
        pollReceipt200 = pollReceipt(HttpStatus.OK);

        goysReceipt = gosysReceipt();
        fordeltReceipt = fordelt();
        when(restOperations.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class)))
                .thenReturn(pollReceipt202);
        sender = sender();
    }

    private FordelSøknadSender sender() {
        MottakConfiguration mottakConfig = new MottakConfiguration();
        PDFElementRenderer jalla1 = new PDFElementRenderer();
        SøknadTextFormatter jalla2 = new SøknadTextFormatter(mottakConfig.landkoder(),
                mottakConfig.kvitteringstekster());
        ForeldrepengeInfoRenderer jalla = new ForeldrepengeInfoRenderer(jalla1, jalla2);
        InfoskrivRenderer infoskrivRenderer = new InfoskrivRenderer(jalla1, jalla2);
        ForeldrepengerPDFGenerator fp = new ForeldrepengerPDFGenerator(oppslag, jalla, infoskrivRenderer);
        EngangsstønadPDFGenerator es = new EngangsstønadPDFGenerator(jalla2, jalla1);
        DelegerendePDFGenerator pdfGenerator = new DelegerendePDFGenerator(fp, es);
        InfoskrivPdfExtractor pdfSplitter = new InfoskrivPdfExtractor();

        DomainMapper domainMapper = new DelegerendeDomainMapper(new V3ForeldrepengerDomainMapper(oppslag));
        KonvoluttGenerator konvoluttGenerator = new KonvoluttGenerator(
                new MetdataGenerator(new ObjectMapper()),
                domainMapper,
                pdfGenerator);
        return new FordelSøknadSender(
                new FordelConnection(restOperations, cfg,
                        new ResponseHandler(restOperations, 3, 10000, poller), registry),
                konvoluttGenerator, pdfSplitter, new LoggingHendelseProdusent());
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

    private static ResponseEntity<ForsendelsesStatusKvittering> fpinfoInnvilget() {
        return okFPSakWith(ForsendelseStatus.INNVILGET);
    }

    private static ResponseEntity<ForsendelsesStatusKvittering> fpinfoPågår() {
        return okFPSakWith(ForsendelseStatus.PÅGÅR);
    }

    private static ResponseEntity<ForsendelsesStatusKvittering> fpinfoFailed() {
        return new ResponseEntity<>(null, BAD_REQUEST);
    }

    private static ResponseEntity<ForsendelsesStatusKvittering> fpinfoNull() {
        return new ResponseEntity<>(null, OK);
    }

    private static ResponseEntity<ForsendelsesStatusKvittering> okFPSakWith(ForsendelseStatus status) {
        return new ResponseEntity<>(new ForsendelsesStatusKvittering(status), OK);
    }

    private static ResponseEntity<FordelKvittering> fordelt() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, FPINFOURISTRING);
        return new ResponseEntity<>(new FPSakFordeltKvittering(JOURNALID, SAKSNR), headers, SEE_OTHER);
    }

    @Test
    public void pollTwiceThenGosys() throws Exception {
        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                goysReceipt);
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), GOSYS);
        assertEquals(kvittering.getJournalId(), JOURNALID);
        assertNull(kvittering.getSaksNr());
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
        verify(restOperations, times(2)).getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class));
    }

    @Test
    public void poll3GivesUp() throws Exception {
        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                pollReceipt200, pollReceipt200);
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), FP_FORDEL_MESSED_UP);
        assertNull(kvittering.getJournalId());
        assertNull(kvittering.getSaksNr());
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
        verify(restOperations, times(3)).getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class));
    }

    @Test
    public void pollOnceThenOKAndFpInfoOK() throws Exception {
        Kvittering value = new Kvittering(LeveranseStatus.PÅGÅR, null, "42");
        value.setJournalId(JOURNALID);
        value.setSaksNr(SAKSNR);
        when(poller.poll(eq(FPINFOURI), any(StopWatch.class), any(Duration.class), any(FPSakFordeltKvittering.class)))
                .thenReturn(value);

        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(restOperations.getForEntity(eq(FPINFOURI), eq(ForsendelsesStatusKvittering.class))).thenReturn(
                fpinfoPågår(),
                fpinfoPågår(),
                fpinfoInnvilget());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getJournalId(), JOURNALID);
        assertEquals(kvittering.getSaksNr(), SAKSNR);
    }

    @Test
    public void pollOnceThenOkAndNoFpInfo() throws Exception {
        Kvittering value = new Kvittering(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK, null, "42");
        value.setJournalId(JOURNALID);
        value.setSaksNr(SAKSNR);
        when(poller.poll(eq(FPINFOURI), any(StopWatch.class), any(Duration.class), any(FPSakFordeltKvittering.class)))
                .thenReturn(value);
        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(restOperations.getForEntity(eq(FPINFOURI), eq(ForsendelsesStatusKvittering.class)))
                .thenReturn(fpinfoNull());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), SENDT_OG_FORSØKT_BEHANDLET_FPSAK);
        assertEquals(kvittering.getJournalId(), JOURNALID);
        assertEquals(kvittering.getSaksNr(), SAKSNR);
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
        verify(restOperations, times(2)).getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class));
    }

    @Test
    public void pollOnceThenOkAndFpInfoOngoing() throws Exception {
        Kvittering value = new Kvittering(LeveranseStatus.PÅGÅR, null, "42");
        value.setJournalId(JOURNALID);
        value.setSaksNr(SAKSNR);
        when(poller.poll(eq(FPINFOURI), any(StopWatch.class), any(Duration.class), any(FPSakFordeltKvittering.class)))
                .thenReturn(value);

        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(restOperations.getForEntity(eq(FPINFOURI), eq(ForsendelsesStatusKvittering.class)))
                .thenReturn(fpinfoPågår());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), PÅGÅR);
        assertEquals(kvittering.getJournalId(), JOURNALID);
        assertEquals(kvittering.getSaksNr(), SAKSNR);
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
    }

    @Test
    public void pollNoLocation() throws Exception {
        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt202,
                pollReceiptNoLocation());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), FP_FORDEL_MESSED_UP);
        verify(restOperations).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class));
    }

    @Test
    public void pollOnceThenOKFpInfoFail() throws Exception {
        Kvittering value = new Kvittering(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK, null, "42");
        value.setJournalId(JOURNALID);
        value.setSaksNr(SAKSNR);
        when(poller.poll(eq(FPINFOURI), any(StopWatch.class), any(Duration.class), any(FPSakFordeltKvittering.class)))
                .thenReturn(value);

        when(restOperations.getForEntity(eq(FPFORDELPOLLURI), eq(FordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(restOperations.getForEntity(eq(FPINFOURI), eq(ForsendelsesStatusKvittering.class)))
                .thenReturn(fpinfoFailed());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), SENDT_OG_FORSØKT_BEHANDLET_FPSAK);
        assertEquals(kvittering.getJournalId(), JOURNALID);
        assertEquals(kvittering.getSaksNr(), SAKSNR);
    }

    @Test
    public void unexpectedStatusCode() throws Exception {
        when(restOperations.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class)))
                .thenReturn(fpfordelPollReceiptError());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), FP_FORDEL_MESSED_UP);
        assertNull(kvittering.getJournalId());
        assertNull(kvittering.getSaksNr());
        verify(restOperations, never()).getForEntity(eq(POSTURI), eq(FordelKvittering.class));
    }

    @Test
    public void testNullBody() throws Exception {
        when(restOperations.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FordelKvittering.class)))
                .thenReturn(nullBody());
        Kvittering kvittering = sender.søk(foreldrepengeSøknad(DEFAULT_VERSJON), person(),
                new SøknadEgenskap(SøknadType.INITIELL_FORELDREPENGER));
        assertEquals(kvittering.getLeveranseStatus(), FP_FORDEL_MESSED_UP);
        assertNull(kvittering.getJournalId());
        assertNull(kvittering.getSaksNr());
        verify(restOperations, never()).getForEntity(eq(POSTURI), eq(FordelKvittering.class));
    }
}
