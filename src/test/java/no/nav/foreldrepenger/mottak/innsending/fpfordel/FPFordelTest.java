package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.net.URI;
import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.TestUtils;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FPFordelTest {

    @Mock
    private RestTemplate template;
    @Mock
    private FPFordelConfig cfg;

    @Test
    public void exampleTest() throws JsonProcessingException {
        Mockito.when(cfg.isEnabled()).thenReturn(true);
        Mockito.when(cfg.getUri()).thenReturn("http://some.host.for.fpfordel");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "http://some.host.for.fpfordel/poll/id");
        FPFordelConnection connection = new FPFordelConnection(template, cfg, new FPFordelResponseHandler(template, 2));
        ResponseEntity<FPFordelKvittering> pollReceppt = new ResponseEntity<>(
                new FPFordelPendingKvittering(Duration.ofMillis(100)), headers, HttpStatus.OK);
        ResponseEntity<FPFordelKvittering> goysReceipt = new ResponseEntity<>(
                new FPFordelGosysKvittering("999"), HttpStatus.OK);
        Mockito.when(
                template.postForEntity(Mockito.any(URI.class), Mockito.any(HttpEntity.class),
                        Mockito.eq(FPFordelKvittering.class)))
                .thenReturn(pollReceppt);
        Mockito.when(
                template.getForEntity(Mockito.anyString(),
                        Mockito.eq(FPFordelKvittering.class)))
                .thenReturn(pollReceppt, goysReceipt);
        MottakConfiguration cfg = new MottakConfiguration();
        ForeldrepengerPDFGenerator pdfGenerator = new ForeldrepengerPDFGenerator(cfg.landkoder(),
                cfg.kvitteringstekster());
        FPFordelSøknadGenerator søknadGenerator = new FPFordelSøknadGenerator();
        FPFordelKonvoluttGenerator konvoluttGenerator = new FPFordelKonvoluttGenerator(
                new FPFordelMetdataGenerator(new ObjectMapper()),
                søknadGenerator,
                pdfGenerator);
        FPFordelSøknadSender sender = new FPFordelSøknadSender(connection, konvoluttGenerator,
                new CallIdGenerator("jalla"));
        Kvittering kvittering = sender.send(ForeldrepengerTestUtils.foreldrepenger(), TestUtils.person());
        System.out.println(kvittering);
    }

}
