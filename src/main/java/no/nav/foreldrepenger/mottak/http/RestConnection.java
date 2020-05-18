package no.nav.foreldrepenger.mottak.http;

import java.net.URI;

import org.springframework.http.ResponseEntity;

public interface RestConnection {
    <T> T getForObject(URI uri, Class<T> responseType);

    <T> T getForObject(URI uri, Class<T> responseType, boolean throwOnNotFound);

    <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType, boolean doThrow);

    <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType);

}
