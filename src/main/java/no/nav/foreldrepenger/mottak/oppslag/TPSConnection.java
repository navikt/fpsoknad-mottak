package no.nav.foreldrepenger.mottak.oppslag;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.http.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.http.PingEndpointAware;

@Component
public class TPSConnection extends AbstractRestConnection implements PingEndpointAware {
    public static final Logger LOG = LoggerFactory.getLogger(TPSConnection.class);
    private final OppslagConfig cfg;

    public TPSConnection(RestOperations restOperations, OppslagConfig config) {
        super(restOperations);
        this.cfg = config;
    }

    public boolean isBrukPdl() {
        return cfg.isBrukPdl();
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return cfg.pingEndpoint();
    }

    @Override
    public String name() {
        return cfg.name();
    }

    @Deprecated
    public Navn navn(String id) {
        return getForObject(cfg.navnUri(id), Navn.class);
    }

    @Deprecated
    public Fødselsnummer fnr(AktørId aktørId) {
        return getForObject(cfg.fnrUri(aktørId), Fødselsnummer.class, true);
    }

    @Deprecated
    Person søker() {
        LOG.trace("Henter søker");
        Person søker = getForObject(cfg.personUri(), Person.class);
        søker.setAktørId(getForObject(cfg.aktørUri(), AktørId.class));
        return søker;
    }

    @Deprecated
    AktørId aktørId(Fødselsnummer fnr) {
        return getForObject(cfg.aktørFnrUri(fnr), AktørId.class, true);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [cfg=" + cfg + "]";
    }
}
