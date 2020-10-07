package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.stereotype.Service;

@Service
public class PDLTjeneste {

    private final PDLConnection connection;

    public PDLTjeneste(PDLConnection connection) {
        this.connection = connection;
    }

    public PDLPerson person() {
        return connection.hentPerson();
    }

}
