package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.http.RetryAware;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.PersonDTO;

@Service
public class PDLTjeneste implements RetryAware {

    private final PDLConnection connection;

    public PDLTjeneste(PDLConnection connection) {
        this.connection = connection;
    }

    public PersonDTO person() {
        return connection.hentPerson();
    }

    public Navn navn(String id) {
        return connection.navn(id);
    }
}
