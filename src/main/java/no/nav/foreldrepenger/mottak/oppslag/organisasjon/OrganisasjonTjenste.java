package no.nav.foreldrepenger.mottak.oppslag.organisasjon;

import org.springframework.stereotype.Service;

@Service
public class OrganisasjonTjenste {

    private final OrganisasjonConnection connection;

    public OrganisasjonTjenste(OrganisasjonConnection connection) {
        this.connection = connection;
    }

    public String organisasjonsNavn(String orgnr) {
        return connection.organisasjonsNavn(orgnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection + "]";
    }
}
