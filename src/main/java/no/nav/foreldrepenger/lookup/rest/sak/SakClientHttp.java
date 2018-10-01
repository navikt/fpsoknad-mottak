package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.ytelser.Sak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
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
    public List<Sak> sakerFor(AktorId aktor, String oidcToken) {
        log.info("Querying Sak service at " + sakBaseUrl);
        String samlToken = stsClient.exchangeForSamlToken(oidcToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Saml " + encode(samlToken));
        headers.set("X-Correlation-ID", "bogus");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<List<RemoteSak>> response = restTemplate.exchange(
            sakBaseUrl + "?aktoerId=" + aktor.getAktør() + "&applikasjon=IT00&tema=FOR",
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

    private String encode(String samlToken) {
        try {
            return Base64.getEncoder().encodeToString(samlToken.getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
