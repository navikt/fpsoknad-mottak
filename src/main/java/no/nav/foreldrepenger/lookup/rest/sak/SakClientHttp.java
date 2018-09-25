package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.stream.Collectors.*;

public class SakClientHttp implements SakClient {

    private RestTemplate restTemplate;

    private String sakBaseUrl;

    public SakClientHttp(String sakBaseUrl, RestTemplate restTemplate) {
        this.sakBaseUrl = sakBaseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Sak> sakerFor(Fødselsnummer fnr) {
        List<RemoteSak> remoteSaker = restTemplate.exchange(
            sakBaseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<RemoteSak>>() {
            }
        ).getBody();

        return remoteSaker.stream().
            map(RemoteSakMapper::map)
            .collect(toList());
    }

}
