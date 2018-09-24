package no.nav.foreldrepenger.mottak.http;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.innsending.AbstractRestConnection;
import no.nav.foreldrepenger.mottak.pdf.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.util.FnrExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static java.util.Collections.*;

@Service
public class OppslagService extends AbstractRestConnection implements Oppslag {

    private static final String AKTØR = "/oppslag/aktor";
    private static final String AKTØRFNR = "/oppslag/aktorfnr";
    private static final String FNR = "/oppslag/fnr";
    private static final String PERSON = "/person";
    private static final String ARBEID = "/arbeidsforhold";

    private static final Logger LOG = LoggerFactory.getLogger(OppslagService.class);
    private final URI baseURI;
    private final FnrExtractor extractor;

    public OppslagService(@Value("${oppslag.baseuri:http://fpsoknad-oppslag/api}") URI baseURI,
            RestTemplate template, FnrExtractor extractor) {
        super(template);
        this.baseURI = baseURI;
        this.extractor = extractor;
    }

    @Override
    public Person getSøker() {
        Person søker = oppslag(PERSON, Person.class);
        søker.aktørId = oppslag(AKTØR, AktorId.class);
        return søker;
    }

    private <T> T oppslag(String pathSegment, Class<T> clazz) {
        URI uri = UriComponentsBuilder.fromUri(baseURI).pathSegment(pathSegment).build().toUri();
        LOG.info("Slår opp {} fra {}", pathSegment.toLowerCase(), uri);
        return template.getForObject(uri, clazz);
    }

    @Override
    public AktorId getAktørId() {
        return getAktørId(new Fødselsnummer(extractor.fnrFromToken()));
    }

    @Override
    public AktorId getAktørId(Fødselsnummer fnr) {
        URI uri = UriComponentsBuilder.fromUri(baseURI).pathSegment(AKTØRFNR).queryParam("fnr", fnr.getFnr()).build()
                .toUri();
        LOG.info("Slår opp {} fra {}", AKTØRFNR.toLowerCase(), uri);
        return template.getForObject(uri, AktorId.class);
    }

    @Override
    public Fødselsnummer getFnr(AktorId aktørId) {
        URI uri = UriComponentsBuilder.fromUri(baseURI).pathSegment(FNR).queryParam("aktorId", aktørId.getId()).build()
                .toUri();
        LOG.info("Slår opp {} fra {}", FNR.toLowerCase(), uri);
        return template.getForObject(uri, Fødselsnummer.class);
    }

    @Override
    public List<Arbeidsforhold> getArbeidsforhold() {
        URI uri = UriComponentsBuilder.fromUri(baseURI).pathSegment(ARBEID).build().toUri();
        LOG.info("Henter arbeidsforhold fra {}", uri);
        try {
            List<Arbeidsforhold> arbeidsforhold = template.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Arbeidsforhold>>() {
                }
            ).getBody();
            LOG.info("Fant {} arbeidsforhold", arbeidsforhold.size());
            return arbeidsforhold;
        } catch (Exception ex) {
            LOG.warn("Error while looking up arbeidsforhold", ex);
            return emptyList();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", baseURI=" + baseURI + "]";
    }

    @Override
    public URI pingEndpoint() {
        return UriComponentsBuilder.fromUri(baseURI).pathSegment("/oppslag/ping").build().toUri();
    }

}
