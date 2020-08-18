package no.nav.foreldrepenger.mottak.oppslag.sak;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.mottak.util.Constants.INFOTRYGD;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.mottak.util.StringUtil.encode;
import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

public class SakClientHttp implements SakClient {

    private static final Logger LOG = LoggerFactory.getLogger(SakClientHttp.class);

    private final RestTemplate restOperations;
    private final URI sakBaseUrl;
    private final StsClient stsClient;
    private final TokenUtil tokenUtil;

    public SakClientHttp(URI sakBaseUrl, @Qualifier("sak") RestTemplate restOperations, StsClient stsClient,
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
        return sisteSakFra(Optional.ofNullable(response.getBody()).orElse(emptyList()));
    }

    private HttpEntity<String> request() {
        return new HttpEntity<>(headers(stsClient.oidcToSamlToken(tokenUtil.getToken())));
    }

    private static List<Sak> sisteSakFra(List<RemoteSak> saker) {
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

    private ResponseEntity<List<RemoteSak>> sakerFor(String aktor, String tema, HttpEntity<String> request) {
        URI url = uri(sakBaseUrl, queryParams(aktor, tema));
        LOG.info(CONFIDENTIAL, "headers " + request.getHeaders());
        LOG.info(CONFIDENTIAL, "auth header " + request.getHeaders().get(AUTHORIZATION));
        LOG.info(CONFIDENTIAL, "Interceptors " + restOperations.getInterceptors());
        return restOperations.exchange(
                url,
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
        LOG.info(CONFIDENTIAL, "Setter token " + encode(samlToken));
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        LOG.info(CONFIDENTIAL, "Headers er: " + headers);
        return headers;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restOperations=" + restOperations + ", sakBaseUrl=" + sakBaseUrl
                + ", stsClient=" + stsClient + ", tokenUtil=" + tokenUtil + "]";
    }

}
