package no.nav.foreldrepenger.lookup.rest.sak;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.lookup.EnvUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class SakClientHttp implements SakClient {

    private static final Logger LOG = LoggerFactory.getLogger(SakClientHttp.class);

    private final RestTemplate restTemplate;

    private final String sakBaseUrl;

    private final StsClient stsClient;

    public SakClientHttp(String sakBaseUrl, RestTemplate restTemplate, StsClient stsClient) {
        this.sakBaseUrl = sakBaseUrl;
        this.restTemplate = restTemplate;
        this.stsClient = stsClient;
    }

    @Override
    @Timed("lookup.sak")
    public List<Sak> sakerFor(AktorId aktor, String oidcToken) {
        LOG.info("henter saker på " + sakBaseUrl);

        String samlToken = stsClient.exchangeForSamlToken(oidcToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Saml " + encode(samlToken));
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        ResponseEntity<List<RemoteSak>> response = restTemplate.exchange(
            sakBaseUrl + "?aktoerId=" + aktor.getAktør() + "&applikasjon=IT01&tema=FOR",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<RemoteSak>>() {
            });
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error while querying Sak, got status " + response.getStatusCode());
        }
        List<RemoteSak> saker = Optional.ofNullable(response.getBody()).orElse(emptyList());

        LOG.info("Fant {} saker", saker.size());
        LOG.info(EnvUtil.CONFIDENTIAL, "{}", saker);

        Sak sisteSak = saker.stream()
            .map(RemoteSakMapper::map)
            .filter(s -> s.getOpprettet().isAfter(now().minusYears(3)))
            .max(comparing(Sak::getOpprettet))
            .orElse(null);

        return sisteSak != null ? singletonList(sisteSak) : emptyList();
    }

    private String encode(String samlToken) {
        try {
            return Base64.getEncoder().encodeToString(samlToken.getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restTemplate=" + restTemplate + ", sakBaseUrl=" + sakBaseUrl
            + ", stsClient=" + stsClient + "]";
    }

}
