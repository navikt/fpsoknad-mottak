package no.nav.foreldrepenger.mottak.oppslag.pdl;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.Navn;
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

    public Navn navn() {
        return connection.navnFor();
    }

    public Navn navn(String id) {
        return connection.navnFor(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }
}
