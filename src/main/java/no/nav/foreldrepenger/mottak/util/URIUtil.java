package no.nav.foreldrepenger.mottak.util;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

public final class URIUtil {
    private URIUtil() {
    }

    public static URI uri(URI base, String path) {
        return uri(base, path, null);
    }

    public static URI uri(URI base, String path, HttpHeaders queryParams) {
        return builder(base, path, queryParams)
                .build()
                .toUri();
    }

    public static UriComponentsBuilder builder(URI base, String path, HttpHeaders queryParams) {
        return UriComponentsBuilder
                .fromUri(base)
                .pathSegment(path)
                .queryParams(queryParams);
    }

    public static HttpHeaders queryParams(String key, String value) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, taint(value));
        return queryParams;
    }

    private static String taint(String value) {
        if (!value.matches("[a-zA-Z0-9]++"))
            throw new IllegalArgumentException(value);
        return value;
    }

    public static HttpHeaders queryParams(String key, String value, String key1, String value1) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, taint(value));
        queryParams.add(key1, taint(value1));
        return queryParams;
    }
}
