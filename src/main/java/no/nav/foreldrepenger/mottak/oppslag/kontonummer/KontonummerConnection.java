package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;

@Component
public class KontonummerConnection extends AbstractRestConnection {

    public KontonummerConnection(RestOperations restOperations, KontonummerConfig cfg) {
        super(restOperations, cfg);
    }

    public Bankkonto kontonr() {
        return getForObject(config.getBaseUri(), Bankkonto.class);
    }
}
