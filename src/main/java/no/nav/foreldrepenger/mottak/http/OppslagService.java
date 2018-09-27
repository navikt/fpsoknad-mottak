package no.nav.foreldrepenger.mottak.http;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.pdf.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.util.FnrExtractor;

@Service
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "false", matchIfMissing = true)
public class OppslagService implements Oppslag {

    private static final String AKTØR = "/oppslag/aktor";
    private static final String AKTØRFNR = "/oppslag/aktorfnr";
    private static final String FNR = "/oppslag/fnr";
    private static final String PERSON = "/person";
    private static final String ARBEID = "/arbeidsforhold";

    private static final Logger LOG = LoggerFactory.getLogger(OppslagService.class);
    private final OppslagConnection connection;
    private final FnrExtractor extractor;

    public OppslagService(OppslagConnection connection, FnrExtractor extractor) {
        this.connection = connection;
        this.extractor = extractor;
    }

    @Override
    public Person getSøker() {
        Person søker = oppslag(PERSON, Person.class);
        søker.aktørId = oppslag(AKTØR, AktorId.class);
        return søker;
    }

    private <T> T oppslag(String pathSegment, Class<T> clazz) {
        URI uri = UriComponentsBuilder.fromUri(connection.baseURI()).pathSegment(pathSegment).build().toUri();
        LOG.info("Slår opp {} fra {}", pathSegment.toLowerCase(), uri);
        T respons = connection.getTemplate().getForObject(uri, clazz);
        LOG.info(CONFIDENTIAL, "Fikk respons {}", respons);
        return respons;
    }

    @Override
    public AktorId getAktørId() {
        return getAktørId(new Fødselsnummer(extractor.fnrFromToken()));
    }

    @Override
    public AktorId getAktørId(Fødselsnummer fnr) {
        URI uri = UriComponentsBuilder.fromUri(connection.baseURI()).pathSegment(AKTØRFNR)
                .queryParam("fnr", fnr.getFnr()).build()
                .toUri();
        LOG.info("Slår opp {} fra {}", AKTØRFNR.toLowerCase(), uri);
        AktorId respons = connection.getTemplate().getForObject(uri, AktorId.class);
        LOG.info(CONFIDENTIAL, "Fikk respons {}", respons);
        return respons;
    }

    @Override
    public Fødselsnummer getFnr(AktorId aktørId) {
        URI uri = UriComponentsBuilder.fromUri(connection.baseURI()).pathSegment(FNR)
                .queryParam("aktorId", aktørId.getId()).build()
                .toUri();
        LOG.info("Slår opp {} fra {}", FNR.toLowerCase(), uri);
        Fødselsnummer respons = connection.getTemplate().getForObject(uri, Fødselsnummer.class);
        LOG.info(CONFIDENTIAL, "Fikk respons {}", respons);
        return respons;
    }

    @Override
    public List<Arbeidsforhold> getArbeidsforhold() {
        URI uri = UriComponentsBuilder.fromUri(connection.baseURI()).pathSegment(ARBEID).build().toUri();
        LOG.info("Henter arbeidsforhold fra {}", uri);
        try {
            List<Arbeidsforhold> arbeidsforhold = connection.getTemplate().exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Arbeidsforhold>>() {
                    }).getBody();
            LOG.info("Fant {} arbeidsforhold", arbeidsforhold.size());
            return arbeidsforhold;
        } catch (Exception ex) {
            LOG.warn("Error while looking up arbeidsforhold", ex);
            return emptyList();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", extractor=" + extractor + "]";
    }

}
