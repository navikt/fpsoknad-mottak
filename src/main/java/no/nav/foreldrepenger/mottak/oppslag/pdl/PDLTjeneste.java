package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.http.RetryAware;

@Service
public class PDLTjeneste implements RetryAware {

    private final PDLConnection connection;

    public PDLTjeneste(PDLConnection connection) {
        this.connection = connection;
    }

    public PDLPerson person() {
        return connection.hentPerson();
    }

}
