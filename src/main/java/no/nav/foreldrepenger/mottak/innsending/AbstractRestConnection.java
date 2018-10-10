package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.foreldrepenger.mottak.http.errorhandling.RemoteUnavailableException;

public abstract class AbstractRestConnection {

    protected final RestTemplate template;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestConnection.class);

    public abstract URI pingEndpoint();

    public abstract boolean isEnabled();

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
            LOG.warn("Kunne ikke pinge på {}", pingEndpoint, e);
            throw new RemoteUnavailableException(e);
        }
    }

    protected static HttpHeaders queryParams(String key, String value) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, value);
        return queryParams;
    }

    protected <T> T getForObject(URI uri, Class<T> responseType) {
        return getForObject(uri, responseType, false);
    }

    protected <T> T getForObject(URI uri, Class<T> responseType, boolean doThrow) {
        try {
            return getAndLog(uri, responseType);
        } catch (HttpStatusCodeException e) {
            HttpStatus code = e.getStatusCode();
            switch (code) {
            case NOT_FOUND:
                if (doThrow) {
                    LOG.warn("{} resulterte i {}, kaster videre", uri, code);
                    throw e;
                }
                LOG.trace("{} resulterte i {}, returnerer null", uri, code);
                return null;
            default:
                LOG.trace("{} resulterte i {} ({}), kaster videre", uri, e.getStatusCode(), e.getStatusText());
                throw e;
            }
        } catch (RestClientException e) {
            LOG.warn("Kunne ikke hente respons", e);
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
        LOG.trace("Henter fra URI {}", uri);
        T respons = template.getForObject(uri, responseType);
        LOG.info("Fikk respons OK");
        LOG.info(CONFIDENTIAL, "{}", respons);
        return respons;
    }
}
