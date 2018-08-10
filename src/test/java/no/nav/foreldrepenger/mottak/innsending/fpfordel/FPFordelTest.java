package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.INNVILGET;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅGÅR;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.PÅ_VENT;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_FORSØKT_BEHANDLET_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.foreldrepenger;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
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
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.http.Oppslag;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FPFordelTest {

    private static final AktorId AKTØRID = new AktorId("1111111111");
    private static final String FPFORDELURIBASE = "http://some.host.for.fpfordel";
    private static final String FPFORDELPOLLURISTRING = FPFORDELURIBASE + "/poll/id";
    private static final String FPINFOURISTRING = "http://some.host.for.fpinfo/status";

    private static final URI FPFORDELPOLLURI = URI.create(FPFORDELPOLLURISTRING);
    private static final URI POSTURI = URI.create(FPFORDELURIBASE + "/fpfordel/api/dokumentforsendelse");

    private static final URI FPINFOURI = URI.create(FPINFOURISTRING);

    private static final String JOURNALID = "999";
    private static final String SAKSNR = "666";
    @Mock
    private RestTemplate template;
    @Mock
    private Oppslag oppslag;
    @Mock
    private FPFordelConfig cfg;

    private ResponseEntity<FPFordelKvittering> pollReceipt202, pollReceipt200;
    private ResponseEntity<FPFordelKvittering> goysReceipt;
    private ResponseEntity<FPFordelKvittering> fordeltReceipt;

    private FPFordelSøknadSender sender;

    @Before
    public void before() {
        when(cfg.isEnabled()).thenReturn(true);
        when(oppslag.getAktørId(any(Fødselsnummer.class))).thenReturn(AKTØRID);
        when(cfg.getUri()).thenReturn(FPFORDELURIBASE);

        pollReceipt202 = pollReceipt(HttpStatus.ACCEPTED);
        pollReceipt200 = pollReceipt(HttpStatus.OK);

        goysReceipt = gosysReceipt();
        fordeltReceipt = fordelt();
        when(template.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(pollReceipt202);
        sender = sender();
    }

    private FPFordelSøknadSender sender() {
        MottakConfiguration mottakConfig = new MottakConfiguration();
        ForeldrepengerPDFGenerator pdfGenerator = new ForeldrepengerPDFGenerator(mottakConfig.landkoder(),
                mottakConfig.kvitteringstekster());

        FPFordelSøknadGenerator søknadGenerator = new FPFordelSøknadGenerator(oppslag);
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
        headers.add(LOCATION, FPFORDELPOLLURISTRING);
        return new ResponseEntity<>(new FPFordelPendingKvittering(Duration.ofMillis(100)), headers, status);
    }

    private static ResponseEntity<FPFordelKvittering> pollReceiptNoLocation() {
        return new ResponseEntity<>(new FPFordelPendingKvittering(Duration.ofMillis(100)), ACCEPTED);
    }

    private static ResponseEntity<FPFordelKvittering> nullBody() {
        return new ResponseEntity<>(null, OK);
    }

    private static ResponseEntity<FPFordelKvittering> fpfordelPollReceiptError() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, FPFORDELPOLLURISTRING);
        return new ResponseEntity<>(new FPFordelPendingKvittering(Duration.ofMillis(100)), headers, BAD_REQUEST);
    }

    private static ResponseEntity<FPSakKvittering> fpinfoInnvilget() {
        return okFPSakWith(FPSakStatus.INNVILGET);
    }

    private static ResponseEntity<FPSakKvittering> fpinfoPågår() {
        return okFPSakWith(FPSakStatus.PÅGÅR);
    }

    private static ResponseEntity<FPSakKvittering> fpinfoFailed() {
        return new ResponseEntity<>(null, BAD_REQUEST);
    }

    private static ResponseEntity<FPSakKvittering> fpinfoNull() {
        return new ResponseEntity<>(null, OK);
    }

    private static ResponseEntity<FPSakKvittering> okFPSakWith(FPSakStatus status) {
        return new ResponseEntity<>(new FPSakKvittering(status, JOURNALID, SAKSNR), OK);
    }

    private static ResponseEntity<FPFordelKvittering> fordelt() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, FPINFOURISTRING);
        return new ResponseEntity<>(new FPSakFordeltKvittering(JOURNALID, SAKSNR), headers, SEE_OTHER);
    }

    @Test
    public void pollTwiceThenGosys() throws Exception {
        when(template.getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                goysReceipt);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(PÅ_VENT));
        assertThat(kvittering.getJournalId(), is(JOURNALID));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class));
        verify(template, times(2)).getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class));
    }

    @Test
    public void poll3GivesUp() throws Exception {
        when(template.getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                pollReceipt200, pollReceipt200);
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(PÅ_VENT));
        assertThat(kvittering.getJournalId(), is(nullValue()));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class));
        verify(template, times(3)).getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class));
    }

    @Test
    public void pollOnceThenOKAndFpInfoOK() throws Exception {
        when(template.getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(template.getForEntity(eq(FPINFOURI), eq(FPSakKvittering.class))).thenReturn(fpinfoPågår(), fpinfoPågår(),
                fpinfoInnvilget());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(INNVILGET));
        assertThat(kvittering.getJournalId(), is(JOURNALID));
        assertThat(kvittering.getSaksNr(), is(SAKSNR));
    }

    @Test
    public void pollOnceThenOkAndNoFpInfo() throws Exception {
        when(template.getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(template.getForEntity(eq(FPINFOURI), eq(FPSakKvittering.class))).thenReturn(fpinfoNull());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(SENDT_OG_FORSØKT_BEHANDLET_FPSAK));
        assertThat(kvittering.getJournalId(), is(JOURNALID));
        assertThat(kvittering.getSaksNr(), is(SAKSNR));
        verify(template).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class));
        verify(template, times(2)).getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class));
        verify(template).getForEntity(eq(FPINFOURI), eq(FPSakKvittering.class));

    }

    @Test
    public void pollOnceThenOkAndFpInfoOngoing() throws Exception {
        when(template.getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(template.getForEntity(eq(FPINFOURI), eq(FPSakKvittering.class))).thenReturn(fpinfoPågår());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(PÅGÅR));
        assertThat(kvittering.getJournalId(), is(JOURNALID));
        assertThat(kvittering.getSaksNr(), is(SAKSNR));
        verify(template).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class));
        verify(template, times(3)).getForEntity(eq(FPINFOURI), eq(FPSakKvittering.class));
    }

    @Test
    public void pollNoLocation() throws Exception {
        when(template.getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt202,
                pollReceiptNoLocation());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(FP_FORDEL_MESSED_UP));
        verify(template).postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class));
    }

    @Test
    public void pollOnceThenOKFpInfoFail() throws Exception {
        when(template.getForEntity(eq(FPFORDELPOLLURI), eq(FPFordelKvittering.class))).thenReturn(pollReceipt200,
                fordeltReceipt);
        when(template.getForEntity(eq(FPINFOURI), eq(FPSakKvittering.class))).thenReturn(fpinfoFailed());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(SENDT_OG_FORSØKT_BEHANDLET_FPSAK));
        assertThat(kvittering.getJournalId(), is(JOURNALID));
        assertThat(kvittering.getSaksNr(), is(SAKSNR));
    }

    @Test
    public void unexpectedStatusCode() throws Exception {
        when(template.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(fpfordelPollReceiptError());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(FP_FORDEL_MESSED_UP));
        assertThat(kvittering.getJournalId(), is(nullValue()));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template, never()).getForEntity(eq(POSTURI), eq(FPFordelKvittering.class));
    }

    @Test
    public void testNullBody() throws Exception {
        when(template.postForEntity(eq(POSTURI), any(HttpEntity.class), eq(FPFordelKvittering.class)))
                .thenReturn(nullBody());
        Kvittering kvittering = sender.send(foreldrepenger(), person());
        assertThat(kvittering.getLeveranseStatus(), is(FP_FORDEL_MESSED_UP));
        assertThat(kvittering.getJournalId(), is(nullValue()));
        assertThat(kvittering.getSaksNr(), is(nullValue()));
        verify(template, never()).getForEntity(eq(POSTURI), eq(FPFordelKvittering.class));
    }
}
