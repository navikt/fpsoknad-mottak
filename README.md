# fpsoknad-oppslag

Slår opp info fra diverse interne registre for å assistere selvbetjent søknad om foreldrepenger og engangsstønad

### For å bygge:
Standard Maven-opplegg, `mvn compile|test|package|install` osv. Det bygges en kjørbar "fatjar"

Som default kjøres kun enhetstester. For å inkludere de saktegående testene, bruk `-Palltests`

### For å kjøre lokalt:

Start no.nav.foreldrepenger.oppslag.OppslagApplication

Default konfigurasjon er lagt i application.properties.

### For å kjøre i et testmiljø med registre tilgjengelig: 
 
Få tak i en Java truststore med gyldige sertifikater for aktuelt testmiljø.

`java -jar fpsoknad-oppslag-<version>.jar -Djavax.net.ssl.trustStore=/path/til/truststore -Djavax.net.ssl.trustStorePassword=........`
 
