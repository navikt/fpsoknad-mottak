package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SakClientHttp implements SakClient {

    private static final Logger log = LoggerFactory.getLogger(SakClientHttp.class);

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
        log.info("Querying Sak service");
        String samlToken = stsClient.exchangeForSamlToken(oidcToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Saml " + samlToken);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<List<RemoteSak>> response = restTemplate.exchange(
            sakBaseUrl,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<RemoteSak>>() {
            }
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error while querying Sak, got status " + response.getStatusCode());
        }

        log.info("Found {} saker", response.getBody().size());

        return response.getBody().stream().
            map(RemoteSakMapper::map)
            .collect(toList());
    }

}
