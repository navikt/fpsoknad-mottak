package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(SpringExtension.class)
class ArbeidsforholdConnectionTest {
    private static MockWebServer mockWebServer;

    @MockBean
    private PDLConnection pdlConnection;
    private OrganisasjonConnection organisasjonConnection;
    private ArbeidsforholdConnection arbeidsforholdConnection;

    private static final String DEFAULT_RESPONSE_EEREG =
        """
        {
            "navn": {
                "navnelinje1": "Fake Bedrift AS"
                }
        }
        """;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(63631);
    }

    @BeforeEach
    void setupConnection() {
        var baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        var webClient = WebClient.builder().baseUrl(baseUrl).build();
        var organisasjonConfig = new OrganisasjonConfig(URI.create(baseUrl), "/v1/organisasjon/{orgnr}", true);
        organisasjonConnection = new OrganisasjonConnection(webClient, pdlConnection, organisasjonConfig);
        var arbeidsforholdConfig = new ArbeidsforholdConfig(URI.create(baseUrl), "/ping",
            "v1/arbeidstaker/arbeidsforhold", true, Period.of(3,0,0),
            false);
        arbeidsforholdConnection = new ArbeidsforholdConnection(webClient, arbeidsforholdConfig, organisasjonConnection);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void verifiserAtOrdinærtArbeidsforholdMedSluttDatoOgOrgnummerBlirMappetKorrekt() {
        var bodyAareg = """
            [
              {
                "ansettelsesperiode": {
                  "bruksperiode": {
                    "fom": "2015-01-06T21:44:04.748",
                    "tom": "2015-12-06T19:45:04"
                  },
                  "periode": {
                    "fom": "2014-07-01",
                    "tom": "2015-12-31"
                  },
                  "sluttaarsak": "arbeidstakerHarSagtOppSelv",
                  "sporingsinformasjon": {
                    "endretAv": "Z990693",
                    "endretKilde": "AAREG",
                    "endretKildereferanse": "referanse-fra-kilde",
                    "endretTidspunkt": "2018-09-19T12:11:20.79",
                    "opprettetAv": "srvappserver",
                    "opprettetKilde": "EDAG",
                    "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
                    "opprettetTidspunkt": "2018-09-19T12:10:58.059"
                  },
                  "varslingskode": "ERKONK"
                },
                "antallTimerForTimeloennet": [
                  {
                    "antallTimer": 37.5,
                    "periode": {
                      "fom": "2014-07-01",
                      "tom": "2015-12-31"
                    },
                    "rapporteringsperiode": "2018-05",
                    "sporingsinformasjon": {
                      "endretAv": "Z990693",
                      "endretKilde": "AAREG",
                      "endretKildereferanse": "referanse-fra-kilde",
                      "endretTidspunkt": "2018-09-19T12:11:20.79",
                      "opprettetAv": "srvappserver",
                      "opprettetKilde": "EDAG",
                      "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
                      "opprettetTidspunkt": "2018-09-19T12:10:58.059"
                    }
                  }
                ],
                "arbeidsavtaler": [
                  {
                    "ansettelsesform": "fast",
                    "antallTimerPrUke": 37.5,
                    "arbeidstidsordning": "ikkeSkift",
                    "beregnetAntallTimerPrUke": 37.5,
                    "bruksperiode": {
                      "fom": "2015-01-06T21:44:04.748",
                      "tom": "2015-12-06T19:45:04"
                    },
                    "gyldighetsperiode": {
                      "fom": "2014-07-01",
                      "tom": "2015-12-31"
                    },
                    "sistLoennsendring": "string",
                    "sistStillingsendring": "string",
                    "sporingsinformasjon": {
                      "endretAv": "Z990693",
                      "endretKilde": "AAREG",
                      "endretKildereferanse": "referanse-fra-kilde",
                      "endretTidspunkt": "2018-09-19T12:11:20.79",
                      "opprettetAv": "srvappserver",
                      "opprettetKilde": "EDAG",
                      "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
                      "opprettetTidspunkt": "2018-09-19T12:10:58.059"
                    },
                    "stillingsprosent": 49.5,
                    "type": "Forenklet",
                    "yrke": "2130123"
                  }
                ],
                "arbeidsforholdId": "abc-321",
                "arbeidsgiver": {
                  "type": "Organisasjon",
                  "organisasjonsnummer": "999999999"
                },
                "arbeidstaker": {
                  "aktoerId": "1234567890",
                  "offentligIdent": "31126700000"
                },
                "innrapportertEtterAOrdningen": false,
                "navArbeidsforholdId": 123456,
                "opplysningspliktig": {
                  "type": "Organisasjon"
                },
                "permisjonPermitteringer": [
                  {
                    "periode": {
                      "fom": "2014-07-01",
                      "tom": "2015-12-31"
                    },
                    "permisjonPermitteringId": "123-xyz",
                    "prosent": 50.5,
                    "sporingsinformasjon": {
                      "endretAv": "Z990693",
                      "endretKilde": "AAREG",
                      "endretKildereferanse": "referanse-fra-kilde",
                      "endretTidspunkt": "2018-09-19T12:11:20.79",
                      "opprettetAv": "srvappserver",
                      "opprettetKilde": "EDAG",
                      "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
                      "opprettetTidspunkt": "2018-09-19T12:10:58.059"
                    },
                    "type": "permisjonMedForeldrepenger",
                    "varslingskode": "string"
                  }
                ],
                "registrert": "2018-09-18T11:12:29",
                "sistBekreftet": "2018-09-19T12:10:31",
                "sporingsinformasjon": {
                  "endretAv": "Z990693",
                  "endretKilde": "AAREG",
                  "endretKildereferanse": "referanse-fra-kilde",
                  "endretTidspunkt": "2018-09-19T12:11:20.79",
                  "opprettetAv": "srvappserver",
                  "opprettetKilde": "EDAG",
                  "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
                  "opprettetTidspunkt": "2018-09-19T12:10:58.059"
                },
                "type": "ordinaertArbeidsforhold",
                "utenlandsopphold": [
                  {
                    "landkode": "JPN",
                    "periode": {
                      "fom": "2014-07-01",
                      "tom": "2015-12-31"
                    },
                    "rapporteringsperiode": "2017-12",
                    "sporingsinformasjon": {
                      "endretAv": "Z990693",
                      "endretKilde": "AAREG",
                      "endretKildereferanse": "referanse-fra-kilde",
                      "endretTidspunkt": "2018-09-19T12:11:20.79",
                      "opprettetAv": "srvappserver",
                      "opprettetKilde": "EDAG",
                      "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
                      "opprettetTidspunkt": "2018-09-19T12:10:58.059"
                    }
                  }
                ]
              }
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(bodyAareg)
            .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse()
            .setBody(DEFAULT_RESPONSE_EEREG)
            .addHeader("Content-Type", "application/json"));

        var arbeidsforhold = arbeidsforholdConnection.hentArbeidsforhold();
        assertThat(arbeidsforhold).hasSize(1);
        var enkeltArbeidsforhold = arbeidsforhold.get(0);
        assertThat(enkeltArbeidsforhold.getArbeidsgiverId()).isEqualTo("999999999");
        assertThat(enkeltArbeidsforhold.getArbeidsgiverIdType()).isEqualTo("orgnr");
        assertThat(enkeltArbeidsforhold.getArbeidsgiverNavn()).isEqualTo("Fake Bedrift AS");
        assertThat(enkeltArbeidsforhold.getFrom()).isEqualTo(LocalDate.parse("2014-07-01"));
        assertThat(enkeltArbeidsforhold.getTo()).isPresent().get().isEqualTo(LocalDate.parse("2015-12-31"));
        assertThat(enkeltArbeidsforhold.getStillingsprosent()).isNull();
    }

    @Test
    void verifiserAtArbeidsforholdMedPrivatArbeidsgiverOgAktivtArbeidsforholdBlirHentetKorrekt() {
        var body = """
            [
              {
                "ansettelsesperiode": {
                  "bruksperiode": {
                    "fom": "2015-01-06T21:44:04.748"
                  },
                  "periode": {
                    "fom": "2014-07-01"
                  }
                },
                "arbeidsavtaler": [
                  {
                    "ansettelsesform": "fast",
                    "antallTimerPrUke": 37.5,
                    "arbeidstidsordning": "ikkeSkift",
                    "beregnetAntallTimerPrUke": 37.5,
                    "bruksperiode": {
                      "fom": "2015-01-06T21:44:04.748",
                      "tom": "2015-12-06T19:45:04"
                    },
                    "gyldighetsperiode": {
                      "fom": "2014-07-01"
                    },
                    "sistLoennsendring": "string",
                    "sistStillingsendring": "string",
                    "sporingsinformasjon": {
                      "endretAv": "Z990693",
                      "endretKilde": "AAREG",
                      "endretKildereferanse": "referanse-fra-kilde",
                      "endretTidspunkt": "2018-09-19T12:11:20.79",
                      "opprettetAv": "srvappserver",
                      "opprettetKilde": "EDAG",
                      "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
                      "opprettetTidspunkt": "2018-09-19T12:10:58.059"
                    },
                    "stillingsprosent": 49.5,
                    "type": "Forenklet",
                    "yrke": "2130123"
                  }
                ],
                "arbeidsforholdId": "abc-321",
                "arbeidsgiver": {
                  "type": "Person",
                  "offentligIdent": "22222233333"
                },
                "arbeidstaker": {
                  "aktoerId": "1234567890",
                  "offentligIdent": "31126700000"
                },
                "registrert": "2018-09-18T11:12:29",
                "sistBekreftet": "2018-09-19T12:10:31"
              }
            ]
            """;
        mockWebServer.enqueue(new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/json"));
        when(pdlConnection.navnFor(any())).thenReturn(new Navn("Per", "", "Pål"));

        var arbeidsforhold = arbeidsforholdConnection.hentArbeidsforhold();
        assertThat(arbeidsforhold).hasSize(1);
        var enkeltArbeidsforhold = arbeidsforhold.get(0);
        assertThat(enkeltArbeidsforhold.getArbeidsgiverId()).isEqualTo("22222233333");
        assertThat(enkeltArbeidsforhold.getArbeidsgiverIdType()).isEqualTo("fnr");
        assertThat(enkeltArbeidsforhold.getArbeidsgiverNavn()).isNotNull();
        assertThat(enkeltArbeidsforhold.getFrom()).isEqualTo(LocalDate.parse("2014-07-01"));
        assertThat(enkeltArbeidsforhold.getTo()).isNotPresent();
        assertThat(enkeltArbeidsforhold.getStillingsprosent()).isEqualTo(new ProsentAndel(49.5));
    }

    @Test
    void verifiserAtIngenBodyGirEnTomArbeidsforholdsListeOgIkkeException() {
        mockWebServer.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json"));

        var arbeidsforhold = arbeidsforholdConnection.hentArbeidsforhold();
        assertThat(arbeidsforhold)
            .isNotNull()
            .isEmpty();
    }

    @Test
    void verifiserAtTomBodyGirEnTomArbeidsforholdsListeOgIkkeException() {
        mockWebServer.enqueue(new MockResponse()
            .setBody("[]")
            .addHeader("Content-Type", "application/json"));

        var arbeidsforhold = arbeidsforholdConnection.hentArbeidsforhold();
        assertThat(arbeidsforhold)
            .isNotNull()
            .isEmpty();
    }

    @Test
    void verifiserAtWebclientPropagerer4xxExceptions() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(403)
            .addHeader("Content-Type", "application/json"));
        assertThrows(WebClientResponseException.Forbidden.class, () -> arbeidsforholdConnection.hentArbeidsforhold());
    }

    @Test
    void verifiserAtWebclientPropagerer5xxExceptions() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(500)
            .addHeader("Content-Type", "application/json"));

        assertThrows(WebClientResponseException.InternalServerError.class, () -> arbeidsforholdConnection.hentArbeidsforhold());
    }
}
