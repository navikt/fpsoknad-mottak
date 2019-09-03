fpsoknad-mottak
================

Mottar søknader om svangerskapspenger, foreldrepenger og engangsstønad fra frontend og sender dem videre inn i NAV for behandling.

# Komme i gang

### For å kjøre lokalt:

Sett ekstraparameter `-parameters` til javac (I IntelliJ: Settings > Build, Execution, Deployment > Compiler > Java Compiler > Additional command line parameters).
Dette for at man skal slippe å annotere alle constructor-parametre med @JsonProperty("xyz").

Start no.nav.foreldrepenger.mottak.MottakApplicationLocal. Denne kjører i "local" profilen automatisk. 

### For å kjøre i et internt testmiljø med registre tilgjengelig: 
 
Få tak i en Java truststore med gyldige sertifikater for aktuelt miljø.

`java -jar fpsoknad-mottak-<version>.jar -Djavax.net.ssl.trustStore=/path/til/truststore -Djavax.net.ssl.trustStorePassword=........`

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes til:

* nav.team.bris@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #bris.
