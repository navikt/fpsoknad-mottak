package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepenger;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SEE_OTHER;

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
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FPFordelTest {

    private static final String POLLURISTRING = "http://some.host.for.fpfordel/poll/id";
    private static final String FPSAKURISTRING = "http://some.host.for.fpsak/status";

    private static final URI POLLURI = URI.create(POLLURISTRING);
    private static final URI FPSAKURI = URI.create(FPSAKURISTRING);

    private static final String JOURNALID = "999";
    private static final String SAKSNR = "666";
    @Mock
    private RestTemplate template;
    @Mock
    private FPFordelConfig cfg;

    private ResponseEntity<FPFordelKvittering> pollReceipt202, pollReceipt200;
    private ResponseEntity<FPFordelKvittering> goysReceipt;
    private ResponseEntity<FPFordelKvittering> fordeltReceipt;

    private FPFordelSøknadSender sender;

    @Before
    public void before() {
        when(cfg.isEnabled()).thenReturn(true);
        when(cfg.getUri()).thenReturn("http://some.host.for.fpfordel");

        pollReceipt202 = pollReceipt(HttpStatus.ACCEPTED);
        pollReceipt200 = pollReceipt(HttpStatus.OK);

        goysReceipt = gosysReceipt();
        fordeltReceipt = fordelt();
        when(template.postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(pollReceipt202);
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
                new FPFordelConnection(template, cfg, new FPFordelResponseHandler(template, 3)),
                konvoluttGenerator,
                new CallIdGenerator("Nav-CallId"));
    }

    private static ResponseEntity<FPFordelKvittering> gosysReceipt() {
        return new ResponseEntity<>(new FPFordelGosysKvittering(JOURNALID), OK);
    }

    private static ResponseEntity<FPFordelKvittering> pollReceipt(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, POLLURISTRING);
        return new ResponseEntity<>(new FPFordelPendingKvittering(Duration.ofMillis(100)), headers, status);
    }

    private static ResponseEntity<FPFordelKvittering> pollReceiptNoLocation() {
        return new ResponseEntity<>(new FPFordelPendingKvittering(Duration.ofMillis(100)), ACCEPTED);
    }

    private static ResponseEntity<FPFordelKvittering> nullBody() {
        return new ResponseEntity<>(null, OK);
    }

    private static ResponseEntity<FPFordelKvittering> pollReceiptError() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, POLLURISTRING);
        return new ResponseEntity<>(new FPFordelPendingKvittering(Duration.ofMillis(100)), headers, BAD_REQUEST);
    }

    private static ResponseEntity<FPSakKvittering> innvilget() {
        return okFPSakWith(FPSakStatus.INNVILGET);
    }

    private static ResponseEntity<FPSakKvittering> fpinfoFailed() {
        return new ResponseEntity<>(null, BAD_REQUEST);
    }

    private static ResponseEntity<FPSakKvittering> okFPSakWith(FPSakStatus status) {
        return new ResponseEntity<>(new FPSakKvittering(status, JOURNALID, SAKSNR), OK);
    }

    private static ResponseEntity<FPFordelKvittering> fordelt() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, FPSAKURISTRING);
        return new ResponseEntity<>(new FPSakFordeltKvittering(JOURNALID, SAKSNR), headers, SEE_OTHER);
    }

    @Test
    public void pollTwiceThenGosys() throws Exception {
        when(template.getForEntity(any(URI.class), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                goysReceipt);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(PÅ_VENT));
        assertThat(JOURNALID, is(kvittering.getJournalId()));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template).postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class));
        verify(template, times(2)).getForEntity(eq(POLLURI), eq(FPFordelKvittering.class));
    }

    @Test
    public void poll3GivesUp() throws Exception {
        when(template.getForEntity(eq(POLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                pollReceipt200, pollReceipt200);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertEquals(PÅ_VENT, kvittering.getLeveranseStatus());
        assertThat(kvittering.getJournalId(), is(nullValue()));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template).postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class));
        verify(template, times(3)).getForEntity(eq(POLLURI), eq(FPFordelKvittering.class));
    }

    @Test
    public void pollOnceThenOKAndFpInfoOK() throws Exception {
        when(template.getForEntity(eq(POLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(template.getForEntity(eq(FPSAKURI), eq(FPSakKvittering.class))).thenReturn(innvilget());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(INNVILGET, is(kvittering.getLeveranseStatus()));
        assertThat(JOURNALID, is(kvittering.getJournalId()));
        assertThat(SAKSNR, is(kvittering.getSaksNr()));
    }

    @Test
    public void pollOnceThenOkAndNoFpInfo() throws Exception {
        when(template.getForEntity(eq(POLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK, is(kvittering.getLeveranseStatus()));
        assertThat(JOURNALID, is(kvittering.getJournalId()));
        assertThat(SAKSNR, is(kvittering.getSaksNr()));
        verify(template).postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class));
        verify(template, times(2)).getForEntity(eq(POLLURI), eq(FPFordelKvittering.class));
    }

    @Test
    public void pollNoLocation() throws Exception {
        when(template.getForEntity(eq(POLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt202,
                pollReceiptNoLocation());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(FP_FORDEL_MESSED_UP, is(kvittering.getLeveranseStatus()));
        verify(template).postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class));
    }

    @Test
    public void pollOnceThenOKFpInfoFail() throws Exception {
        when(template.getForEntity(eq(POLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(template.getForEntity(eq(FPSAKURI), eq(FPSakKvittering.class))).thenReturn(fpinfoFailed());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK, is(kvittering.getLeveranseStatus()));
        assertThat(JOURNALID, is(kvittering.getJournalId()));
        assertThat(SAKSNR, is(kvittering.getSaksNr()));
    }

    @Test
    public void unexpectedStatusCode() throws Exception {
        when(template.postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(pollReceiptError());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(FP_FORDEL_MESSED_UP, is(kvittering.getLeveranseStatus()));
        assertThat(kvittering.getJournalId(), is(nullValue()));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template, never()).getForEntity(any(URI.class), eq(FPFordelKvittering.class));
    }

    @Test
    public void testNullBody() throws Exception {
        when(template.postForEntity(any(URI.class), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(nullBody());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(FP_FORDEL_MESSED_UP, is(kvittering.getLeveranseStatus()));
        assertThat(kvittering.getJournalId(), is(nullValue()));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template, never()).getForEntity(any(URI.class), eq(FPFordelKvittering.class));
    }
}
