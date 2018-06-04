package no.nav.foreldrepenger.mottak.http;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.AktorId;

@Service
public class AktørIdService implements AktørIDLookup {

    private final RestTemplate template;
    private final URI aktørURI;

    public AktørIdService(@Value("${fpoppslag.uri:http://fpsoknad-oppslag/api/oppslag/aktor}") URI aktørURI,
            RestTemplate template) {
        this.template = template;
        this.aktørURI = aktørURI;
    }

    @Override
    public AktorId getAktørId() {
        return template.getForObject(aktørURI, AktorId.class);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", aktørURI=" + aktørURI + "]";
    }
}
