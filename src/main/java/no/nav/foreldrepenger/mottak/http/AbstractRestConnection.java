package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractRestConnection {

    private final RestOperations restOperations;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestConnection.class);

    protected abstract boolean isEnabled();

    public AbstractRestConnection(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    protected String ping(URI uri) {
        return getForObject(uri, String.class);
    }

    protected static HttpHeaders queryParams(String key, String value) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, value);
        return queryParams;
    }

    protected <T> T getForObject(URI uri, Class<T> responseType) {
        return getForObject(uri, responseType, false);
    }

    protected <T> ResponseEntity<T> postForEntity(URI uri, HttpEntity<?> payload, Class<T> responseType) {
        ResponseEntity<T> respons = restOperations.postForEntity(uri, payload, responseType);
        if (respons.hasBody()) {
            LOG.trace(CONFIDENTIAL, "Respons: {}", respons.getBody());
        }
        return respons;
    }

    protected <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType) {
        ResponseEntity<T> respons = restOperations.getForEntity(uri, responseType);
        if (respons.hasBody()) {
            LOG.trace(CONFIDENTIAL, "Respons: {}", respons.getBody());
        }
        return respons;
    }

    protected <T> T getForObject(URI uri, Class<T> responseType, boolean doThrow) {
        try {
            T respons = restOperations.getForObject(uri, responseType);
            if (respons != null) {
                LOG.trace(CONFIDENTIAL, "Respons: {}", respons);
            }
            return respons;
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(NOT_FOUND) && !doThrow) {
                return null;
            }
            throw e;
        }
    }

    protected static URI uri(URI base, String path) {
        return uri(base, path, null);
    }

    protected static URI uri(URI base, String path, HttpHeaders queryParams) {
        return builder(base, path)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    private static UriComponentsBuilder builder(URI base, String path) {
        return UriComponentsBuilder
                .fromUri(base)
                .pathSegment(path);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restOperations=" + restOperations + "]";
    }
}
