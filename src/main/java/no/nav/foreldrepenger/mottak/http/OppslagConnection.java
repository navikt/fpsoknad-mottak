package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.http.OppslagConfig.AKTØR;
import static no.nav.foreldrepenger.mottak.http.OppslagConfig.AKTØRFNR;
import static no.nav.foreldrepenger.mottak.http.OppslagConfig.ARBEID;
import static no.nav.foreldrepenger.mottak.http.OppslagConfig.FNR;
import static no.nav.foreldrepenger.mottak.http.OppslagConfig.PERSON;

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
        return endpointFor(config.getBaseURI(), config.getPingPath());
    }

    public Person getSøker() {
        Person søker = getForObject(endpointFor(config.getBaseURI(), PERSON), Person.class);
        søker.aktørId = getForObject(endpointFor(config.getBaseURI(), AKTØR), AktorId.class);
        return søker;
    }

    public AktorId getAktørId(Fødselsnummer fnr) {
        return getForObject(endpointFor(config.getBaseURI(), AKTØRFNR, queryParams("fnr", fnr.getFnr())),
                AktorId.class, true);
    }

    public Fødselsnummer getFnr(AktorId aktørId) {
        return getForObject(endpointFor(config.getBaseURI(), FNR, queryParams("aktorId", aktørId.getId())),
                Fødselsnummer.class, true);
    }

    public List<Arbeidsforhold> getArbeidsforhold() {
        List<Arbeidsforhold> arbeidsforhold = getForList(endpointFor(config.getBaseURI(), ARBEID),
                Arbeidsforhold.class);
        LOG.info("Fant {} arbeidsforhold", arbeidsforhold.size());
        return arbeidsforhold;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }
}
