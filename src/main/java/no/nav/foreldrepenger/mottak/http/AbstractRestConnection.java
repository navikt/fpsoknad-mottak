package no.nav.foreldrepenger.mottak.http;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.http.errorhandling.NotFoundException;
import no.nav.foreldrepenger.mottak.http.errorhandling.RemoteUnavailableException;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnauthenticatedException;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnauthorizedException;

public abstract class AbstractRestConnection {

    private final RestTemplate template;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestConnection.class);

    public abstract boolean isEnabled();

    public AbstractRestConnection(RestTemplate template) {
        this.template = template;
    }

    public String ping(URI uri) {
        return getForEntity(uri, String.class).getBody();
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
        try {
            return template.postForEntity(uri, payload, responseType);
        } catch (HttpStatusCodeException e) {
            HttpStatus code = e.getStatusCode();
            LOG.warn("Kunne ikke poste entity til {}, status kode var {}", uri, code, e);
            switch (code) {
            case UNAUTHORIZED:
                throw new UnauthorizedException(e);
            case FORBIDDEN:
                throw new UnauthenticatedException(e);
            default:
                throw new RemoteUnavailableException(e);
            }
        } catch (RestClientException e) {
            throw new RemoteUnavailableException(e);
        }
    }

    protected <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType) {
        try {
            ResponseEntity<T> response = template.getForEntity(uri, responseType);
            LOG.trace("Fikk respons OK for {}", uri);
            if (response.hasBody()) {
                LOG.trace(CONFIDENTIAL, "Body: {}", response.getBody());
            }
            return response;
        } catch (HttpStatusCodeException e) {
            HttpStatus code = e.getStatusCode();
            LOG.warn("Fant ingen entitet på {}, status kode var {}", uri, code, e);
            switch (code) {
            case NOT_FOUND:
                throw new NotFoundException(e);
            case UNAUTHORIZED:
                throw new UnauthorizedException(e);
            case FORBIDDEN:
                throw new UnauthenticatedException(e);
            default:
                throw new RemoteUnavailableException(e);
            }
        } catch (RestClientException e) {
            LOG.warn("Fant ingen entitet på {}", uri, e);
            throw new RemoteUnavailableException(e);
        }
    }

    protected <T> T getForObject(URI uri, Class<T> responseType, boolean doThrow) {
        try {
            return getAndLog(uri, responseType);
        } catch (HttpStatusCodeException e) {
            HttpStatus code = e.getStatusCode();
            LOG.warn("Fant intet objekt på {}, status kode var {}", uri, code, e);
            switch (code) {
            case NOT_FOUND:
                if (doThrow) {
                    LOG.trace("kaster NotFoundException videre");
                    throw new NotFoundException(e);
                }
                LOG.trace("Returnerer null");
                return null;
            case UNAUTHORIZED:
                throw new UnauthorizedException(e);
            case FORBIDDEN:
                throw new UnauthenticatedException(e);
            default:
                throw new RemoteUnavailableException(e);
            }
        } catch (RestClientException e) {
            LOG.warn("Fant intet objekt på {}", uri, e);
            throw new RemoteUnavailableException(uri, e);
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

    private <T> T getAndLog(URI uri, Class<T> responseType) {
        T respons = template.getForObject(uri, responseType);
        LOG.trace(CONFIDENTIAL, "{}", respons);
        return respons;
    }
}
