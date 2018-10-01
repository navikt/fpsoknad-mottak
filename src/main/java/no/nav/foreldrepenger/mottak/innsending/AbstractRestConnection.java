package no.nav.foreldrepenger.mottak.innsending;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.http.errorhandling.NotFoundException;
import no.nav.foreldrepenger.mottak.http.errorhandling.RemoteUnavailableException;

public abstract class AbstractRestConnection {

    protected final RestTemplate template;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestConnection.class);

    public abstract URI pingEndpoint();

    public AbstractRestConnection(RestTemplate template) {
        this.template = template;
    }

    public String ping() {
        URI pingEndpoint = pingEndpoint();
        try {
            LOG.info("Pinger {}", pingEndpoint);
            ResponseEntity<String> response = template.getForEntity(pingEndpoint, String.class);
            LOG.info("Fikk response entity {} ({})", response.getBody(), response.getStatusCodeValue());
            return response.getBody();
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke pinge FPInfo på {}", pingEndpoint, e);
            throw new RemoteUnavailableException(e);
        }
    }

    protected <T> List<T> getForList(URI uri, Class<T> clazz) {
        ParameterizedTypeReference<List<T>> type = new ParameterizedTypeReference<List<T>>() {
        };
        try {
            LOG.info("Henter liste med type {}", type.getType());
            List<T> list = template.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    type).getBody();
            LOG.info("Fikk respons liste {} med type {}", list, type.getType().getTypeName());
            return list;
        } catch (Exception ex) {
            LOG.warn("Kunne ikke hente liste {}", type.getType().getTypeName(), ex);
            return emptyList();
        }

    }

    private static UriComponentsBuilder builder(URI base, String path) {
        return UriComponentsBuilder
                .fromUri(base)
                .pathSegment(path);
    }

    protected static URI uri(URI base, String path) {
        return uri(base, path, null);
    }

    protected static URI uri(URI base, String path, HttpHeaders queryParams) {
        URI uri = builder(base, path)
                .queryParams(queryParams)
                .build()
                .toUri();
        LOG.debug("Bruker URI  {}", uri);
        return uri;
    }

    protected static HttpHeaders queryParams(String key, String value) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, value);
        return queryParams;
    }

    protected <T> T getForObject(URI uri, Class<T> responseType) {
        return getForObject(uri, responseType, false);
    }

    protected <T> T getForObject(URI uri, Class<T> responseType, boolean isConfidential) {
        return getForObject(uri, responseType, isConfidential, false);

    }

    protected <T> T getForObject(URI uri, Class<T> responseType, boolean isConfidential, boolean doThrow) {
        try {
            T respons = template.getForObject(uri, responseType);
            if (isConfidential) {
                LOG.info(CONFIDENTIAL, "Fikk respons {}", respons);
            }
            else {
                LOG.info("Fikk respons {}", respons);
            }
            if (doThrow) {
                throw new NotFoundException("Kunne ikke hente fra " + uri);
            }
            return respons;
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke hente respons", e);
            throw new RemoteUnavailableException(uri, e);
        }
    }

}
