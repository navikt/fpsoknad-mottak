package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

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
import no.nav.foreldrepenger.mottak.innsending.PingEndpointAware;

@Component
public class OppslagConnection extends AbstractRestConnection implements PingEndpointAware {
    public static final Logger LOG = LoggerFactory.getLogger(OppslagConnection.class);
    private final OppslagConfig cfg;

    public OppslagConnection(RestOperations restOperations, OppslagConfig config) {
        super(restOperations);
        this.cfg = config;
    }

    @Override
    public String ping() {
        return ping(pingEndpoint());
    }

    @Override
    public URI pingEndpoint() {
        return uri(cfg.getBaseUri(), cfg.getPingPath());
    }

    public Person hentSøker() {
        LOG.trace("Henter søker");
        Person søker = getForObject(uri(cfg.getBaseUri(), cfg.getPersonPath()), Person.class);
        søker.setAktørId(getForObject(uri(cfg.getBaseUri(), cfg.getAktørPath()), AktørId.class));
        return søker;
    }

    public AktørId hentAktørId(Fødselsnummer fnr) {
        return getForObject(
                uri(cfg.getBaseUri(), cfg.getAktørFnrPath(), queryParams("fnr", fnr.getFnr())), AktørId.class, true);
    }

    public Navn hentNavn(Fødselsnummer fnr) {
        return getForObject(uri(cfg.getBaseUri(), cfg.getPersonNavnPath(), queryParams("fnr", fnr.getFnr())),
                Navn.class);
    }

    public Fødselsnummer hentFnr(AktørId aktørId) {
        return getForObject(
                uri(cfg.getBaseUri(), cfg.getFnrPath(), queryParams("aktorId", aktørId.getId())), Fødselsnummer.class,
                true);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [cfg=" + cfg + "]";
    }

    @Override
    public String name() {
        return "fpsoknad-oppslag";
    }
}
