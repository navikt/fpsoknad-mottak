# fpsoknad-oppslag

Slår opp info fra diverse interne registre for å assistere selvbetjent søknad om foreldrepenger og engangsstønad

### For å bygge:
Standard Maven-opplegg, `mvn compile|test|package|install` osv. Det bygges en kjørbar "fatjar"

### For å kjøre lokalt:
##### (alle punktene under utføres automagisk på [NAIS](https://nais.github.io))
Sett følgende miljøvariabler:

 * SECURITYTOKENSERVICE_URL
 * FPSELVBETJENING_USERNAME (for STS)
 * FPSELVBETJENING_PASSWORD (for STS)
 * VIRKSOMHET_INNTEKT_V3_ENDPOINTURL
 * AKTOER_V2_ENDPOINTURL
 * VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL
 * VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL
 * VIRKSOMHET_PERSON_V3_ENDPOINTURL
 * VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL
 * VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL
 * VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL
 * VIRKSOMHET_MEDLEMSKAP_V2_ENDPOINTURL
 
Last ned no.nav.modig:modig-testcertificates (fra intern Nexus) og pakk ut fila truststore.jts et dertil egnet sted på disken din.
 
Start opp med følgende VM-options (a.k.a. -D):
 * javax.net.ssl.trustStore=/der/du pakka/ut/i/forrige/steg/truststore.jts
 * javax.net.ssl.trustStorePassword=........
