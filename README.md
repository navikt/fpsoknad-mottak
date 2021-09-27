![](https://github.com/navikt/fpsoknad-mottak/workflows/Deployment%20pipeline%20q1/badge.svg) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-mottak&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_fpsoknad-mottak)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-mottak&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=navikt_fpsoknad-mottak)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-mottak&metric=code_smells)](https://sonarcloud.io/dashboard?id=navikt_fpsoknad-mottak)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-mottak&metric=security_rating)](https://sonarcloud.io/dashboard?id=navikt_fpsoknad-mottak)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-mottak&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_fpsoknad-mottak)


fpsoknad-mottak
================

Mottar søknader om svangerskapspenger, foreldrepenger og engangsstønad fra frontend og sender dem videre inn i NAV for behandling.   

### For å kjøre lokalt: 

Sett ekstraparameter `-parameters` til javac (I IntelliJ: Settings > Build, Execution, Deployment > Compiler > Java Compiler > Additional command line parameters).
Dette for at man skal slippe å annotere alle constructor-parametre med @JsonProperty("xyz").

Start no.nav.foreldrepenger.mottak.MottakApplicationLocal.
### For å kjøre i et internt testmiljø med registre tilgjengelig: 
 
Få tak i en Java truststore med gyldige sertifikater for aktuelt miljø.

`java -jar fpsoknad-mottak-<version>.jar -Djavax.net.ssl.trustStore=/path/til/truststore -Djavax.net.ssl.trustStorePassword=........`

---

# Henvendelser

Spørsmål kan rettes til:

* nav.team.foreldrepenger@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #teamforeldrepenger. 
