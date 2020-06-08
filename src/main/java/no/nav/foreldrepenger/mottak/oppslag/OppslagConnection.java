package no.nav.foreldrepenger.mottak.oppslag;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.oppslag.OppslagConfig.ORGNR;
import static no.nav.foreldrepenger.mottak.util.URIUtil.queryParams;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.Arbeidsforhold;

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
        return uri(cfg.getBaseURI(), cfg.getPingPath());
    }

    public Person hentSøker() {
        LOG.trace("Henter søker");
        Person søker = getForObject(uri(cfg.getBaseURI(), cfg.getPersonPath()), Person.class);
        søker.setAktørId(getForObject(uri(cfg.getBaseURI(), cfg.getAktørPath()), AktørId.class));
        return søker;
    }

    public AktørId hentAktørId(Fødselsnummer fnr) {
        return getForObject(
                uri(cfg.getBaseURI(), cfg.getAktørFnrPath(), queryParams("fnr", fnr.getFnr())), AktørId.class, true);
    }

    public Navn hentNavn(String fnr) {
        return getForObject(uri(cfg.getBaseURI(), cfg.getPersonNavnPath(), queryParams("fnr", fnr)), Navn.class);
    }

    public Fødselsnummer hentFnr(AktørId aktørId) {
        return getForObject(
                uri(cfg.getBaseURI(), cfg.getFnrPath(), queryParams("aktorId", aktørId.getId())), Fødselsnummer.class,
                true);
    }

    List<Arbeidsforhold> hentArbeidsforhold() {
        LOG.trace("Henter arbeidsforhold");
        return Optional.ofNullable(getForObject(uri(cfg.getBaseURI(), cfg.getArbeidsforholdPath()),
                Arbeidsforhold[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
    }

    public String organisasjonsNavn(String orgnr) {
        LOG.trace("Henter navn for organisasjon {}", orgnr);
        return getForObject(uri(cfg.getBaseURI(), cfg.getOrgNavnPath(), queryParams(ORGNR, orgnr)), String.class);
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
