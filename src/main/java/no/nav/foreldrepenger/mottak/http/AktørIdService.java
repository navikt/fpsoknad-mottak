package no.nav.foreldrepenger.mottak.http;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.AktorId;

@Service
public class AktørIdService {

    private final RestTemplate template;
    private final URI aktørURI;

    public AktørIdService(@Value("${fpmottak.uri:http://fpsoknad-mottak/api/aktor}") URI aktørURI,
            RestTemplate template) {
        this.template = template;
        this.aktørURI = aktørURI;
    }

    public AktorId getAktørId() {
        return template.getForObject(aktørURI, AktorId.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", aktørURI=" + aktørURI + "]";
    }
}
