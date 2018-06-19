package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_MOTATT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepenger;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FPFordelTest {

    private static final String JOURNALID = "999";
    private static final String SAKSNR = "666";
    @Mock
    private RestTemplate template;
    @Mock
    private FPFordelConfig cfg;

    private ResponseEntity<FPFordelKvittering> pollReceipt;
    private ResponseEntity<FPFordelKvittering> goysReceipt;
    private ResponseEntity<FPFordelKvittering> fordeltReceipt;

    private FPFordelSøknadSender sender;

    @Before
    public void before() {
        when(cfg.isEnabled()).thenReturn(true);
        when(cfg.getUri()).thenReturn("http://some.host.for.fpfordel");

        pollReceipt = pollReceipt();
        goysReceipt = gosysReceipt();
        fordeltReceipt = fordelt();

        when(template.postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(pollReceipt);
        sender = sender();
    }

    private FPFordelSøknadSender sender() {
        MottakConfiguration mottakConfig = new MottakConfiguration();

        ForeldrepengerPDFGenerator pdfGenerator = new ForeldrepengerPDFGenerator(mottakConfig.landkoder(),
                mottakConfig.kvitteringstekster());

        FPFordelSøknadGenerator søknadGenerator = new FPFordelSøknadGenerator();
        FPFordelKonvoluttGenerator konvoluttGenerator = new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(new ObjectMapper()),
                søknadGenerator,
                pdfGenerator);
        return new FPFordelSøknadSender(
                new FPFordelConnection(template, cfg, new FPFordelResponseHandler(template, 2)),
                konvoluttGenerator,
                new CallIdGenerator("jalla"));
    }

    private static ResponseEntity<FPFordelKvittering> gosysReceipt() {
        return new ResponseEntity<>(
                new FPFordelGosysKvittering(JOURNALID), HttpStatus.OK);
    }

    private static ResponseEntity<FPFordelKvittering> pollReceipt() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "http://some.host.for.fpfordel/poll/id");
        ResponseEntity<FPFordelKvittering> pollreceipt = new ResponseEntity<>(
                new FPFordelPendingKvittering(Duration.ofMillis(100)), headers, HttpStatus.ACCEPTED);
        return pollreceipt;
    }

    private static ResponseEntity<FPFordelKvittering> nullBody() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private static ResponseEntity<FPFordelKvittering> pollReceiptError() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "http://some.host.for.fpfordel/poll/id");
        ResponseEntity<FPFordelKvittering> pollreceipt = new ResponseEntity<>(
                new FPFordelPendingKvittering(Duration.ofMillis(100)), headers, HttpStatus.BAD_REQUEST);
        return pollreceipt;
    }

    private static ResponseEntity<FPFordelKvittering> fordelt() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "http://some.host.for.fpfordel/poll/id");
        ResponseEntity<FPFordelKvittering> pollreceipt = new ResponseEntity<>(
                new FPSakFordeltKvittering(JOURNALID, SAKSNR), headers, HttpStatus.SEE_OTHER);
        return pollreceipt;
    }

    @Test
    public void pollTwiceThenGosys() throws Exception {
        when(template.getForEntity(anyString(), eq(FPFordelKvittering.class))).thenReturn(pollReceipt, goysReceipt);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertEquals(SENDT_GOSYS, kvittering.getLeveranseStatus());
        assertEquals(JOURNALID, kvittering.getJournalId());
        assertEquals(null, kvittering.getSaksNr());
    }

    @Test
    public void pollThriceGivesUp() throws Exception {
        when(template.getForEntity(anyString(), eq(FPFordelKvittering.class))).thenReturn(pollReceipt, pollReceipt);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertEquals(SENDT_FPSAK, kvittering.getLeveranseStatus());
        assertEquals(null, kvittering.getJournalId());
        assertEquals(null, kvittering.getSaksNr());
    }

    @Test
    public void pollOnceThenOK() throws Exception {
        when(template.getForEntity(anyString(), eq(FPFordelKvittering.class))).thenReturn(pollReceipt, fordeltReceipt);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertEquals(SENDT_OG_MOTATT_FPSAK, kvittering.getLeveranseStatus());
        assertEquals(JOURNALID, kvittering.getJournalId());
        assertEquals(SAKSNR, kvittering.getSaksNr());
    }

    @Test
    public void unexpectedStatusCode() throws Exception {
        when(template.postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(pollReceiptError());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertEquals(FP_FORDEL_MESSED_UP, kvittering.getLeveranseStatus());
        assertEquals(null, kvittering.getJournalId());
        assertEquals(null, kvittering.getSaksNr());
    }

    @Test
    public void testNullBody() throws Exception {
        when(template.postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(nullBody());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertEquals(FP_FORDEL_MESSED_UP, kvittering.getLeveranseStatus());
        assertEquals(null, kvittering.getJournalId());
        assertEquals(null, kvittering.getSaksNr());
    }
}
