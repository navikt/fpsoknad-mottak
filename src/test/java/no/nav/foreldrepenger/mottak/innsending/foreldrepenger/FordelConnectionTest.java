package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.common.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsending.mappers.Mappables.DELEGERENDE;
import static no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils.foreldrepengesøknad;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FPSakFordeltKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.FordelKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.GosysKvittering;
import no.nav.foreldrepenger.common.innsending.foreldrepenger.PendingKvittering;
import no.nav.foreldrepenger.common.innsending.mappers.AktørIdTilFnrConverter;
import no.nav.foreldrepenger.common.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3EngangsstønadDomainMapper;
import no.nav.foreldrepenger.common.innsending.mappers.V3ForeldrepengerDomainMapper;
import no.nav.foreldrepenger.common.util.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.config.JacksonConfiguration;
import no.nav.foreldrepenger.mottak.innsending.mappers.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.pdf.MappablePdfGenerator;
import no.nav.foreldrepenger.mottak.util.JacksonWrapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    JacksonConfiguration.class,
    JacksonWrapper.class,
    MetdataGenerator.class,
    KonvoluttGenerator.class,
    DelegerendeDomainMapper.class,
    V3ForeldrepengerDomainMapper.class,
    V3EngangsstønadDomainMapper.class,
    V1SvangerskapspengerDomainMapper.class
})
class FordelConnectionTest {
    private static final String JOURNALPOSTID = "123456789";
    private static final Saksnummer SAKSNUMMER = new Saksnummer("11122233344");
    private static String baseUrl;

    @MockBean
    private AktørIdTilFnrConverter aktørIdTilFnrConverter;

    @MockBean
    @Qualifier(DELEGERENDE)
    private MappablePdfGenerator mappablePdfGenerator;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KonvoluttGenerator konvoluttGenerator;

    private Konvolutt defaultRequestKonvolutt;
    private static MockWebServer mockWebServer;

    private static FordelConnection fordelConnection;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.create();
        var fordelConfig = new FordelConfig(URI.create(baseUrl), "/innsending", 3);
        fordelConnection = new FordelConnection(webClient, fordelConfig);
    }

    @BeforeEach
    void before() {
        when(mappablePdfGenerator.generer(any(), any(), any())).thenReturn(new byte[0]);
        when(aktørIdTilFnrConverter.konverter(any())).thenReturn(new AktørId("1234"));
        defaultRequestKonvolutt = lagDefaultKonvolutt();
    }

    /*
     * Mottar første en OK 200 med en gosys kvittering
     * Da er innsendingen mottatt og fordelt
     */
    @Test
    void happyCaseGosysFordelingFirstTry() throws JsonProcessingException {
        // Arrange
        var gosysKvittering = new GosysKvittering(JOURNALPOSTID);
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(tilBody(gosysKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        var resultat = fordelConnection.send(defaultRequestKonvolutt);

        // Assert
        assertThat(resultat.journalId()).isEqualTo(gosysKvittering.getJournalpostId());
        assertThat(resultat.saksnummer()).isNull();
    }

    /*
     * Mottar første en SEE_OTHER 303 forventer vi en FpsakKvittering
     * og da innsendingen er mottatt og fordelt
     */
    @Test
    void happyCaseFpsakFordelingFirstTry() throws JsonProcessingException {
        // Arrange
        var fpSakFordeltKvittering = new FPSakFordeltKvittering(JOURNALPOSTID, SAKSNUMMER);
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(303)
            .setBody(tilBody(fpSakFordeltKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        var resultat = fordelConnection.send(defaultRequestKonvolutt);

        // Assert
        assertThat(resultat.journalId()).isEqualTo(fpSakFordeltKvittering.getJournalpostId());
        assertThat(resultat.saksnummer()).isEqualTo(fpSakFordeltKvittering.getSaksnummer());
    }


    /*
     * Mottar første en ACCEPTED 202 pending kvittering (mottatt, men ikke fordelt)
     * Følger redirekt 303 med kvittering om at innsendinger er fordelt til fpsak
     * Da er vi ferdig og returnerer resultat tilbake til bruker
     */
    @Test
    void forsendelseMottattMenIkkeFordeltFPSAKOrdnesOppIVedPollingFørstegang() throws JsonProcessingException {
        // Arrange
        var pendingKvittering = new PendingKvittering(Duration.ofMillis(100));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(202)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json")
            .addHeader(LOCATION, baseUrl + "/api/forsendelse/status?forsendelseId=123456789"));

        var fpSakFordeltKvittering = new FPSakFordeltKvittering(JOURNALPOSTID, SAKSNUMMER);
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(303)
            .setBody(tilBody(fpSakFordeltKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        var resultat = fordelConnection.send(defaultRequestKonvolutt);

        // Assert
        assertThat(resultat.journalId()).isEqualTo(fpSakFordeltKvittering.getJournalpostId());
        assertThat(resultat.saksnummer()).isEqualTo(fpSakFordeltKvittering.getSaksnummer());
    }


    /*
     * Mottar første en ACCEPTED 202 pending kvittering (mottatt, men ikke fordelt)
     * Følger OK 200 med kvittering om at innsendinger er fordelt til gosys
     * Da er vi ferdig og returnerer resultat tilbake til bruker
     */
    @Test
    void forsendelseMottattMenIkkeFordeltGosysPollerStatusEnGangOK() throws JsonProcessingException {
        // Arrange
        var pendingKvittering = new PendingKvittering(Duration.ofMillis(100));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(202)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json")
            .addHeader(LOCATION, baseUrl + "/api/forsendelse/status?forsendelseId=123456789"));

        var gosysKvittering = new GosysKvittering(JOURNALPOSTID);
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(tilBody(gosysKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        var resultat = fordelConnection.send(defaultRequestKonvolutt);

        // Assert
        assertThat(resultat.journalId()).isEqualTo(gosysKvittering.getJournalpostId());
        assertThat(resultat.saksnummer()).isNull();
    }

    /*
     * Mottar første en ACCEPTED 202 pending kvittering (mottatt, men ikke fordelt)
     * Følger redirekt 303 med kvittering om at innsendinger er fordelt til fpsak
     * Da er vi ferdig og returnerer resultat tilbake til bruker
     */
    @Test
    void forsendelseMottattMenIkkeFordeltFPSAKOrdnesOppIVedPollingAndreGang() throws JsonProcessingException {
        // Arrange
        // Fra innsendingsendepuntket
        var pendingKvittering = new PendingKvittering(Duration.ofMillis(100));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(202)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json")
            .addHeader(LOCATION, baseUrl + "/api/forsendelse/status?forsendelseId=123456789"));

        // Fra statusendepunktet
        // 1) PENDING
        // 2) FORDELT I FPSAK
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));
        var fpSakFordeltKvittering = new FPSakFordeltKvittering(JOURNALPOSTID, SAKSNUMMER);
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(303)
            .setBody(tilBody(fpSakFordeltKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        var resultat = fordelConnection.send(defaultRequestKonvolutt);

        // Assert
        assertThat(resultat.journalId()).isEqualTo(fpSakFordeltKvittering.getJournalpostId());
        assertThat(resultat.saksnummer()).isEqualTo(fpSakFordeltKvittering.getSaksnummer());
    }

    /*
     * Mottar første en ACCEPTED 202 pending kvittering (mottatt, men ikke fordelt)
     * Poller men får OK 200 med pending kvittering
     * Poller men får OK 200 med pending kvittering
     * Poller men får OK 200 med pending kvittering
     * Feiler og returnerer UventetFpFordelResponseException siden innsendingen ikke er ikke fordelt
     */
    @Test
    void pollingOverMaxForsøkSkalHiveException() throws JsonProcessingException {
        // Arrange
        // Fra innsendingsendepuntket
        var pendingKvittering = new PendingKvittering(Duration.ofMillis(100));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(202)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json")
            .addHeader(LOCATION, baseUrl + "/api/forsendelse/status?forsendelseId=123456789"));

        // Fra statusendepunktet
        // 1) PENDING
        // 2) PENDING
        // 3) PENDING
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        assertThatThrownBy(() -> fordelConnection.send(defaultRequestKonvolutt))
            .isInstanceOf(UventetPollingStatusFpFordelException.class);
    }

    @Test
    void pendingKvitteringUtenLocationHeaderSkalHiveUventetFpFordelResponseException() throws JsonProcessingException {
        // Arrange
        // Fra innsendingsendepuntket
        var pendingKvittering = new PendingKvittering(Duration.ofMillis(100));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(202)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json"));
            //.addHeader(LOCATION, baseUrl + "/api/forsendelse/status?forsendelseId=123456789"));

        // Act
        assertThatThrownBy(() -> fordelConnection.send(defaultRequestKonvolutt))
            .isInstanceOf(UventetPollingStatusFpFordelException.class);
    }


    @Test
    void forsendelseInneholderUkjentOKStatusFeilerHardt() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(204)
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        assertThatThrownBy(() -> fordelConnection.send(defaultRequestKonvolutt))
            .isInstanceOf(InnsendingFeiletFpFordelException.class);
    }


//    @Test
//    void forsendelseInneholderUkjent5xxStatusFeilerHardt() {
//        // Arrange
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//
//
//        // Act
//        assertThatThrownBy(() -> fordelConnection.send(defaultRequestKonvolutt))
//            .isInstanceOf(InnsendingFeiletFpFordelException.class);
//    }
//
//    /*
//     * Mottar første en ACCEPTED 202 pending kvittering (mottatt, men ikke fordelt)
//     * Følger redirekt 303 med kvittering om at innsendinger er fordelt til fpsak
//     * Da er vi ferdig og returnerer resultat tilbake til bruker
//     */
//    @Test
//    void verifiserAtViHiverUventetFpFordelResponseExceptionVed5xxFeilUnderPolling() throws JsonProcessingException {
//        // Arrange - Fra innsendingsendepuntket
//        var pendingKvittering = new PendingKvittering(Duration.ofMillis(100));
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(202)
//            .setBody(tilBody(pendingKvittering))
//            .addHeader(CONTENT_TYPE, "application/json")
//            .addHeader(LOCATION, baseUrl + "/api/forsendelse/status?forsendelseId=123456789"));
//
//        // Fra statusendepunktet (retry 2 ganger på 5xx feil, og siste faller ut til UventetFpFordelResponseException)
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//        mockWebServer.enqueue(new MockResponse()
//            .setResponseCode(500)
//            .addHeader(CONTENT_TYPE, "application/json"));
//
//        // Act
//        assertThatThrownBy(() -> fordelConnection.send(defaultRequestKonvolutt))
//            .isInstanceOf(UventetPollingStatusFpFordelException.class);
//    }

    @Test
    void verifiserAtViHiverUventetFpFordelResponseExceptionVedNoContentUnderPolling() throws JsonProcessingException {
        // Arrange - Fra innsendingsendepuntket
        var pendingKvittering = new PendingKvittering(Duration.ofMillis(100));
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(202)
            .setBody(tilBody(pendingKvittering))
            .addHeader(CONTENT_TYPE, "application/json")
            .addHeader(LOCATION, baseUrl + "/api/forsendelse/status?forsendelseId=123456789"));

        // Fra statusendepunktet
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(204)
            .addHeader(CONTENT_TYPE, "application/json"));

        // Act
        assertThatThrownBy(() -> fordelConnection.send(defaultRequestKonvolutt))
            .isInstanceOf(UventetPollingStatusFpFordelException.class);
    }

    private Konvolutt lagDefaultKonvolutt() {
        var søknad = foreldrepengesøknad( false, valgfrittVedlegg(ForeldrepengerTestUtils.ID142, LASTET_OPP));
        return konvoluttGenerator.generer(søknad, SøknadEgenskap.of(INITIELL_FORELDREPENGER), new InnsendingPersonInfo(person().navn(), person().aktørId(), person().fnr()));
    }

    private String tilBody(FordelKvittering kvittering) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(kvittering);
    }


    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}
