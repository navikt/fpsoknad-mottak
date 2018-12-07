package no.nav.foreldrepenger.lookup.rest.sak;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.lookup.rest.AbstractRestConnection;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

public class SakClientHttp extends AbstractRestConnection implements SakClient {

    private static final Logger LOG = LoggerFactory.getLogger(SakClientHttp.class);

    private final String sakBaseUrl;

    private final StsClient stsClient;

    public SakClientHttp(String sakBaseUrl, RestOperations restOperations, StsClient stsClient) {
        super(restOperations);
        this.sakBaseUrl = sakBaseUrl;
        this.stsClient = stsClient;
    }

    @Override
    @Timed("lookup.sak")
    public List<Sak> sakerFor(AktorId aktor, String oidcToken) {
        LOG.trace("henter saker på " + sakBaseUrl);

        /*
         * String samlToken = stsClient.exchangeForSamlToken(oidcToken); HttpHeaders
         * headers = new HttpHeaders(); headers.set("Authorization", "Saml " +
         * encode(samlToken)); headers.setContentType(APPLICATION_JSON);
         * headers.setAccept(singletonList(APPLICATION_JSON)); HttpEntity<String>
         * requestEntity = new HttpEntity<>("", headers);
         *
         */
        List<RemoteSak> saker = Optional.ofNullable(
                getForObject(URI.create(sakBaseUrl + "?aktoerId=" + aktor.getAktør() + "&applikasjon=IT01&tema=FOR"),
                        RemoteSak[].class))
                .map(Arrays::asList)
                .orElse(emptyList());
        /*
         * ResponseEntity<List<RemoteSak>> response = operations.exchange( sakBaseUrl +
         * "?aktoerId=" + aktor.getAktør() + "&applikasjon=IT01&tema=FOR",
         * HttpMethod.GET, requestEntity, new
         * ParameterizedTypeReference<List<RemoteSak>>() { }); if
         * (response.getStatusCode() != HttpStatus.OK) { throw new
         * RuntimeException("Error while querying Sak, got status " +
         * response.getStatusCode()); } List<RemoteSak> saker =
         * Optional.ofNullable(response.getBody()).orElse(emptyList());
         */
        LOG.info("Fant {} sak(er)", saker.size());
        if (!saker.isEmpty()) {
            LOG.trace("{}", saker);
        }

        Sak sisteSak = saker.stream()
                .map(RemoteSakMapper::map)
                .filter(s -> s.getOpprettet() != null)
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
    protected boolean isEnabled() {
        // TODO
        return true;
    }

    @Override
    protected URI pingURI() {
        // TODO
        return URI.create("http://www.vg.no");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sakBaseUrl=" + sakBaseUrl + ", stsClient=" + stsClient + "]";
    }
}
