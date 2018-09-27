package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SakClientHttp implements SakClient {

    private RestTemplate restTemplate;

    private String sakBaseUrl;

    private StsClient stsClient;

    public SakClientHttp(String sakBaseUrl, RestTemplate restTemplate, StsClient stsClient) {
        this.sakBaseUrl = sakBaseUrl;
        this.restTemplate = restTemplate;
        this.stsClient = stsClient;
    }

    @Override
    public List<Sak> sakerFor(Fødselsnummer fnr, String oidcToken) {
        String samlToken = stsClient.exchangeForSamlToken(oidcToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Saml " + samlToken);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        List<RemoteSak> remoteSaker = restTemplate.exchange(
            sakBaseUrl,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<RemoteSak>>() {
            }
        ).getBody();

        return remoteSaker.stream().
            map(RemoteSakMapper::map)
            .collect(toList());
    }

}
