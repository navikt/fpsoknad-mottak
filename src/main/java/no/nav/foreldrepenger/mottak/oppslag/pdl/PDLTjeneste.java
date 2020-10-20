package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.http.RetryAware;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;

@Service
public class PDLTjeneste implements RetryAware {

    private final PDLConnection connection;

    public PDLTjeneste(PDLConnection connection) {
        this.connection = connection;
    }

    public SøkerDTO søker() {
        return connection.hentSøker();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }
}
