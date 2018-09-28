package no.nav.foreldrepenger.mottak.http;

import java.net.URI;
import java.util.List;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.pdf.Arbeidsforhold;

@Component
public class OppslagConnection extends AbstractRestConnection {

    public static final Logger LOG = LoggerFactory.getLogger(OppslagConnection.class);

    private final OppslagConfig cfg;

    public OppslagConnection(RestTemplate template, OppslagConfig config) {
        super(template);
        this.cfg = config;
    }

    @Override
    public URI pingEndpoint() {
        return uri(cfg.getBaseURI(), cfg.getPingPath());
    }

    public Person getSøker() {
        Person søker = getForObject(uri(cfg.getBaseURI(), cfg.getPersonPath()), Person.class);
        søker.aktørId = getForObject(uri(cfg.getBaseURI(), cfg.getAktørPath()), AktorId.class);
        MDC.put("Nav-Aktør-Id", søker.aktørId.getId());
        return søker;
    }

    public AktorId getAktørId(Fødselsnummer fnr) {
        return getForObject(
                uri(cfg.getBaseURI(), cfg.getAktørFnrPath(), queryParams("fnr", fnr.getFnr())), AktorId.class, true);
    }

    public Fødselsnummer getFnr(AktorId aktørId) {
        return getForObject(
                uri(cfg.getBaseURI(), cfg.getFnrPath(), queryParams("aktorId", aktørId.getId())), Fødselsnummer.class,
                true);
    }

    public List<Arbeidsforhold> getArbeidsforhold() {
        return getForList(uri(cfg.getBaseURI(), cfg.getArbeidsforholdPath()), Arbeidsforhold.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [cfg=" + cfg + "]";
    }
}
