package no.nav.foreldrepenger.mottak.oppslag.sak;

import static java.time.LocalDate.now;
import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.common.util.Constants.INFOTRYGD;
import static no.nav.foreldrepenger.mottak.util.StringUtil.encode;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

public class SakClientHttp implements SakClient {

    private static final Logger LOG = LoggerFactory.getLogger(SakClientHttp.class);

    private final RestOperations restOperations;
    private final URI sakBaseUrl;
    private final StsClient stsClient;
    private final TokenUtil tokenUtil;

    public SakClientHttp(URI sakBaseUrl, RestOperations restOperations, StsClient stsClient,
            TokenUtil tokenUtil) {
        this.restOperations = restOperations;
        this.sakBaseUrl = sakBaseUrl;
        this.stsClient = stsClient;
        this.tokenUtil = tokenUtil;

    }

    @Override
    public List<Sak> sakerFor(AktørId aktor, String tema) {
        LOG.info("Henter saker for {}", aktor);
        var response = sakerFor(aktor.getId(), tema, request());
        return sisteSakFra(Optional.ofNullable(response.getBody()).orElse(List.of()));

    }

    private HttpEntity<String> request() {
        return new HttpEntity<>(headers(stsClient.oidcToSamlToken(tokenUtil.getToken(), tokenUtil.fnr())));
    }

    private static List<Sak> sisteSakFra(List<RemoteSak> saker) {
        if (!saker.isEmpty()) {
            LOG.info("Fant {} sak(er)", saker.size());
            LOG.trace("{}", saker);
        }

        Sak sisteSak = saker.stream()
                .map(RemoteSakMapper::map)
                .filter(s -> s.getOpprettet() != null)
                .filter(s -> s.getOpprettet().isAfter(now().minusYears(3)))
                .max(comparing(Sak::getOpprettet))
                .orElse(null);
        return sisteSak != null ? List.of(sisteSak) : List.of();
    }

    private ResponseEntity<List<RemoteSak>> sakerFor(String aktor, String tema, HttpEntity<String> request) {
        return restOperations.exchange(
                uri(sakBaseUrl, queryParams(aktor, tema)),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<RemoteSak>>() {
                });

    }

    private static HttpHeaders queryParams(String aktor, String tema) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add("aktoerId", aktor);
        queryParams.add("applikasjon", INFOTRYGD);
        queryParams.add("tema", tema);
        return queryParams;
    }

    private static HttpHeaders headers(String samlToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Saml " + encode(samlToken));
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(List.of(APPLICATION_JSON));
        return headers;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restOperations=" + restOperations + ", sakBaseUrl=" + sakBaseUrl
                + ", stsClient=" + stsClient + ", tokenUtil=" + tokenUtil + "]";
    }

}
