package no.nav.foreldrepenger.mottak.http;

import static no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;

public abstract class AbstractRestConnection implements RetryAware {
    private final RestOperations restOperations;
    private final AbstractConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestConnection.class);

    protected AbstractRestConnection(RestOperations restOperations) {
        this(restOperations, null);
    }

    protected AbstractRestConnection(RestOperations restOperations, AbstractConfig config) {
        this.restOperations = restOperations;
        this.config = config;
    }

    protected String ping(URI uri) {
        return getForObject(uri, String.class);
    }

    protected <T> ResponseEntity<T> postForEntity(URI uri, HttpEntity<?> payload, Class<T> responseType) {
        var respons = restOperations.postForEntity(uri, payload, responseType);
        if (respons.hasBody()) {
            LOG.trace(CONFIDENTIAL, "Respons: {}", respons.getBody());
        }
        return respons;
    }

    public void options(URI uri) {
        restOperations.optionsForAllow(uri);
    }

    public <T> T postForObject(URI uri, Object payload, Class<T> responseType) {
        return restOperations.postForObject(uri, payload, responseType);
    }

    public <T> T getForObject(URI uri, Class<T> responseType) {
        return getForObject(uri, responseType, false);
    }

    public <T> T getForObject(URI uri, Class<T> responseType, boolean required) {
        try {
            T respons = restOperations.getForObject(uri, responseType);
            if (respons != null) {
                LOG.trace(CONFIDENTIAL, "Respons: {}", respons);
            }
            return respons;
        } catch (HttpClientErrorException e) {
            if (NOT_FOUND.equals(e.getStatusCode()) && !required) {
                LOG.info("Fant intet objekt på {}, returnerer null", uri);
                return null;
            }
            throw e;
        }
    }

    public <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType) {
        return getForEntity(uri, responseType, true);
    }

    public <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType, boolean required) {
        try {
            var respons = restOperations.getForEntity(uri, responseType);
            if (respons.hasBody()) {
                LOG.trace(CONFIDENTIAL, "Respons: {}", respons.getBody());
            }
            return respons;
        } catch (HttpClientErrorException e) {
            if (NOT_FOUND.equals(e.getStatusCode()) && !required) {
                LOG.info("Fant ingen entity på {}, returnerer null", uri);
                return null;
            }
            throw e;
        }
    }

    protected String name() {
        return Optional.ofNullable(config)
                .map(AbstractConfig::name)
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restOperations=" + restOperations + "]";
    }
}
