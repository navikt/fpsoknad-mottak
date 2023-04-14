package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.config.JacksonConfiguration;
import no.nav.foreldrepenger.mottak.http.WebClientConfiguration;
import no.nav.foreldrepenger.mottak.oppslag.dkif.DigdirKrrProxyConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.KontoregisterConnection;
import no.nav.foreldrepenger.mottak.oppslag.kontonummer.dto.Konto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(SpringExtension.class)
class PDLConnectionTest {
    private final AktørId AKTØRID_SØKER = new AktørId("99123456789");
    private final Fødselsnummer FØDSELSNUMMER_SØKER = new Fødselsnummer("123456789");

    private static GraphQLWebClient userClient;
    private static GraphQLWebClient systemClient;
    private static PDLConfig cfg;
    private static MockWebServer mockWebServer;

    private PDLConnection pdlConnection;

    @Mock
    private DigdirKrrProxyConnection digdir;
    @Mock
    private KontoregisterConnection kontoregister;
    @Mock
    private TokenUtil tokenUtil;


    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        cfg = new PDLConfig("/", true, URI.create(baseUrl), 24);
        var objectmapper = new JacksonConfiguration().customObjectmapper();
        var konfigurasjoner = new WebClientConfiguration();
        var webclient = WebClient.builder().baseUrl(baseUrl).build();
        userClient = konfigurasjoner.pdlWebClient(webclient, objectmapper);
        systemClient = konfigurasjoner.pdlSystemWebClient(webclient, objectmapper);
    }

    @BeforeEach
    void setup() {
        pdlConnection = new PDLConnection(userClient, systemClient, cfg, digdir, kontoregister, new PDLExceptionGeneratingResponseHander());
    }


    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void hentPersonBarnNyligFødtIkkeDødEllerSkjermetAdresse() {
        when(tokenUtil.autentisertBrukerOrElseThrowException()).thenReturn(FØDSELSNUMMER_SØKER);
        when(digdir.målform()).thenReturn(Målform.NB);
        when(kontoregister.kontonummer()).thenReturn(Konto.UKJENT);

        var fnrBarn = new Fødselsnummer("987654321");
        var fødselsdatoFar = LocalDate.now().minusYears(25);
        var personFar = personOpplysningFar("MANN", fnrBarn, fødselsdatoFar);
        // Hent personopplysninger om far (søker)
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(personFar).addHeader("Content-Type", "application/json"));
        // Hent aktørid for far
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
            .setBody(hentIdentRespons(FØDSELSNUMMER_SØKER, AKTØRID_SØKER))
            .addHeader("Content-Type", "application/json"));

        var fødselsdatoBarn = LocalDate.now().minusDays(6);
        var personBarn = personOpplysningerBarnResponse(fødselsdatoBarn, null, null);
        // Hent personopplysninger om barnet
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(personBarn).addHeader("Content-Type", "application/json"));


        var person = pdlConnection.hentPerson(tokenUtil.autentisertBrukerOrElseThrowException());
        assertThat(person).isNotNull();
        assertThat(person.fnr()).isEqualTo(FØDSELSNUMMER_SØKER);
        assertThat(person.kjønn()).isEqualTo(Kjønn.M);
        assertThat(person.fødselsdato()).isEqualTo(fødselsdatoFar);

        assertThat(person.barn()).hasSize(1);
        var barn = person.barn().iterator().next();
        assertThat(barn.fnr()).isEqualTo(fnrBarn);
    }

    @Test
    void hentPersonBarnSkalIkkeReturnerOpplysningerOmBarnetVedFORTROLIGAdresse() {
        when(tokenUtil.autentisertBrukerOrElseThrowException()).thenReturn(FØDSELSNUMMER_SØKER);
        when(digdir.målform()).thenReturn(Målform.NB);
        when(kontoregister.kontonummer()).thenReturn(Konto.UKJENT);

        var fnrBarn = new Fødselsnummer("987654321");
        var fødselsdatoFar = LocalDate.now().minusYears(25);
        var personFar = personOpplysningFar("MANN", fnrBarn, fødselsdatoFar);
        // Hent personopplysninger om far (søker)
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(personFar).addHeader("Content-Type", "application/json"));
        // Hent aktørid for far
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
            .setBody(hentIdentRespons(FØDSELSNUMMER_SØKER, AKTØRID_SØKER))
            .addHeader("Content-Type", "application/json"));

        var fødselsdatoBarn = LocalDate.now().minusDays(6);
        var personBarn = personOpplysningerBarnResponse(fødselsdatoBarn, null, "STRENGT_FORTROLIG");
        // Hent personopplysninger om barnet
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(personBarn).addHeader("Content-Type", "application/json"));


        var person = pdlConnection.hentPerson(tokenUtil.autentisertBrukerOrElseThrowException());
        assertThat(person).isNotNull();
        assertThat(person.fnr()).isEqualTo(FØDSELSNUMMER_SØKER);
        assertThat(person.kjønn()).isEqualTo(Kjønn.M);
        assertThat(person.fødselsdato()).isEqualTo(fødselsdatoFar);

        assertThat(person.barn()).isEmpty();
    }

    @Test
    void hentNavnFor() {
        var fornavn = "Ola";
        var etternavn = "Normann";
        var body = String.format("""
            {
              "data": {
                "hentPerson": {
                  "navn": [
                    {
                      "fornavn": "Ola",
                      "mellomnavn": null,
                      "etternavn": "Normann"
                    }
                  ]
                }
              }
            }
            """, fornavn, etternavn);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(body).addHeader("Content-Type", "application/json"));
        var navn = pdlConnection.navnFor(FØDSELSNUMMER_SØKER.value());
        assertThat(navn.fornavn()).isEqualTo(fornavn);
        assertThat(navn.mellomnavn()).isNull();
        assertThat(navn.etternavn()).isEqualTo(etternavn);
    }


    @Test
    void hentAktørIDFraGraphQLResponsHappyCase() {
        var body = hentIdentRespons(FØDSELSNUMMER_SØKER, AKTØRID_SØKER);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(body).addHeader("Content-Type", "application/json"));
        var response = pdlConnection.aktørId(FØDSELSNUMMER_SØKER);
        assertThat(response).isEqualTo(AKTØRID_SØKER);
    }

    @Test
    void hentFødselsnummerFraGraphQLResponsHappyCase() {
        var body = hentIdentRespons(FØDSELSNUMMER_SØKER, AKTØRID_SØKER);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(body).addHeader("Content-Type", "application/json"));
        var response = pdlConnection.fnr(AKTØRID_SØKER);
        assertThat(response).isEqualTo(FØDSELSNUMMER_SØKER);
    }

    @Test
    void verifiserAtGraphQLErrorPersonIkkeFunnetBlirMappetTil404() throws IOException {
        var body = """
            {
              "errors": [
                {
                  "message": "Fant ikke person",
                  "locations": [
                    {
                      "line": 2,
                      "column": 5
                    }
                  ],
                  "path": [
                    "hentPerson"
                  ],
                  "extensions": {
                    "code": "not_found",
                    "classification": "ExecutionAborted"
                  }
                }
              ],
              "data": {
                "hentIdenter": null
              }
            }
            """;

        var currentRequestcount = mockWebServer.getRequestCount();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(body).addHeader("Content-Type", "application/json"));

        assertThrows(WebClientResponseException.NotFound.class, () -> pdlConnection.aktørId(FØDSELSNUMMER_SØKER));
        assertThat(mockWebServer.getRequestCount()).isEqualTo(currentRequestcount + 1);
    }


    @Test
    void verifiserAtGraphQLErrorIkkeTilgangBlirMappetTil403() {
        var body = """
            {
              "errors": [
                {
                  "message": "Ikke tilgang til å se person",
                  "locations": [
                    {
                      "line": 30,
                      "column": 5
                    }
                  ],
                  "path": [
                    "hentPerson"
                  ],
                  "extensions": {
                    "code": "unauthorized",
                    "details": {
                      "type": "abac-deny",
                      "cause": "cause-0001-manglerrolle",
                      "policy": "adressebeskyttelse_strengt_fortrolig_adresse"
                    },
                    "classification": "ExecutionAborted"
                  }
                }
              ],
              "data": {
                "hentIdenter": null
              }
            }
            """;

        var currentRequestcount = mockWebServer.getRequestCount();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(body).addHeader("Content-Type", "application/json"));

        assertThrows(WebClientResponseException.Forbidden.class, () -> pdlConnection.aktørId(FØDSELSNUMMER_SØKER));
        assertThat(mockWebServer.getRequestCount()).isEqualTo(currentRequestcount + 1);
    }


    //    @Test
    //    void oppslagSkalRetryPå5xxFeil() {
    //        mockWebServer.enqueue(new MockResponse()
    //            .setResponseCode(500)
    //            .addHeader("Content-Type", "application/json"));
    //        mockWebServer.enqueue(new MockResponse()
    //            .setResponseCode(500)
    //            .addHeader("Content-Type", "application/json"));
    //        mockWebServer.enqueue(new MockResponse()
    //            .setResponseCode(500)
    //            .addHeader("Content-Type", "application/json"));
    //        mockWebServer.enqueue(new MockResponse()
    //            .setResponseCode(500)
    //            .addHeader("Content-Type", "application/json"));
    //
    //        var currentRequestcount = mockWebServer.getRequestCount();
    //
    //        var err = assertThrows(Exception.class, () -> pdlConnection.aktørId(FØDSELSNUMMER_SØKER));
    //        assertThat(Exceptions.isRetryExhausted(err)).isTrue();
    //        assertThat(err.getCause()).isInstanceOf(WebClientResponseException.InternalServerError.class);
    //
    //        assertThat(mockWebServer.getRequestCount()).isEqualTo(currentRequestcount + 4);
    //
    //    }


    private String personOpplysningFar(String kjønn, Fødselsnummer fnrBarn, LocalDate fødselsdatoFar) {
        return String.format("""
            {
                "data": {
                    "hentPerson": {
                    "navn": [
                        {
                            "fornavn": "Ola",
                            "mellomnavn": null,
                            "etternavn": "Normann"
                        }
                    ],
                    "kjoenn": [
                        {
                            "kjoenn": "%s"
                        }
                    ],
                    "foedsel": [
                        {
                            "foedselsdato": "%s"
                        }
                    ],
                    "forelderBarnRelasjon": [
                        {
                            "relatertPersonsIdent": "%s",
                            "relatertPersonsrolle": "BARN",
                            "minRolleForPerson": "FAR"
                        }
                    ]
                    }
                }
            }
            """, kjønn, fødselsdatoFar.format(DateTimeFormatter.ISO_LOCAL_DATE), fnrBarn.value());
    }

    private String personOpplysningerBarnResponse(LocalDate fødselsdatoBarn, LocalDate doedsdato, String adressebeskyttelse) {
        String graderingString = "";
        if (adressebeskyttelse != null) {
            graderingString = String.format("""
                ,
                "adressebeskyttelse": [
                    {
                        "gradering": "%s"
                    }
                ]""", adressebeskyttelse);
        }

        String doedsdatoString = "";
        if (doedsdato != null) {
            doedsdatoString = String.format("""
                ,
                "doedsfall": [
                    {
                        "doedsdato": "%s"
                    }
                ]""", doedsdato.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        return String.format("""
                        {
                "data": {
                    "hentPerson": {
                    "navn": [
                        {
                            "fornavn": "Barn",
                            "mellomnavn": null,
                            "etternavn": "Normann"
                        }
                    ],
                    "kjoenn": [
                        {
                            "kjoenn": "MANN"
                        }
                    ],
                    "foedsel": [
                        {
                            "foedselsdato": "%s"
                        }
                    ],
                    "forelderBarnRelasjon": [
                        {
                            "relatertPersonsIdent": "%s",
                            "relatertPersonsrolle": "FAR",
                            "minRolleForPerson": "BARN"
                        }
                    ]%s%s
                    }
                }
            }
            """, fødselsdatoBarn.format(DateTimeFormatter.ISO_LOCAL_DATE), FØDSELSNUMMER_SØKER.value(), doedsdatoString, graderingString);
    }

    private String hentIdentRespons(Fødselsnummer fnr, AktørId aktørid) {
        return String.format("""
            {
                "errors": [],
                "data": {
                    "hentIdenter": {
                        "identer": [
                            {
                                "ident": "%s",
                                "historisk": false,
                                "gruppe": "FOLKEREGISTERIDENT"
                            },
                            {
                                "ident": "12345678911",
                                "historisk": false,
                                "gruppe": "NPID"
                            },
                            {
                                "ident": "%s",
                                "historisk": false,
                                "gruppe": "AKTORID"
                            }
                        ]
                    }
                }
            }
            """, fnr.value(), aktørid.value());
    }
}
