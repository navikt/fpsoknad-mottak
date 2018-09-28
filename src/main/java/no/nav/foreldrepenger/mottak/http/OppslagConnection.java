package no.nav.foreldrepenger.mottak.http;

import java.net.URI;
import java.util.List;

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

    private final OppslagConfig config;

    public OppslagConnection(RestTemplate template, OppslagConfig config) {
        super(template);
        this.config = config;
    }

    @Override
    public URI pingEndpoint() {
        return uriFrom(config.getBaseURI(), config.getPingPath());
    }

    public Person getSøker() {
        Person søker = getForObject(uriFrom(config.getBaseURI(), config.getPersonPath()), Person.class);
        søker.aktørId = getForObject(uriFrom(config.getBaseURI(), config.getAktørPath()), AktorId.class);
        return søker;
    }

    public AktorId getAktørId(Fødselsnummer fnr) {
        return getForObject(
                uriFrom(config.getBaseURI(), config.getAktørFnrPath(), queryParams("fnr", fnr.getFnr())),
                AktorId.class, true);
    }

    public Fødselsnummer getFnr(AktorId aktørId) {
        return getForObject(
                uriFrom(config.getBaseURI(), config.getFnrPath(), queryParams("aktorId", aktørId.getId())),
                Fødselsnummer.class, true);
    }

    public List<Arbeidsforhold> getArbeidsforhold() {
        return getForList(uriFrom(config.getBaseURI(), config.getArbeidsforholdPath()), Arbeidsforhold.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }
}
