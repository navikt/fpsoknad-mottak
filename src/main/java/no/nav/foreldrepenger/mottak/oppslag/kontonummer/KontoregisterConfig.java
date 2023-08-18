package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Swagger: https://sokos-kontoregister-person.dev.intern.nav.no/api/borger/v1/docs/#/kontoregister.v1/kontooppslag%20med%20get
 */
@ConfigurationProperties(prefix = "kontoregister")
public class KontoregisterConfig {
    private static final String DEFAULT_URI = "https://sokos-kontoregister-person.intern.nav.no";
    private static final String DEFAULT_BASE_PATH = "api/borger/v1/hent-aktiv-konto";
    private final URI baseUri;

    public KontoregisterConfig(@DefaultValue(DEFAULT_URI) URI baseUri) {
        this.baseUri = baseUri;
    }

    URI kontoregisterURI() {
        return uri(getBaseUri(), DEFAULT_BASE_PATH);
    }

    public URI getBaseUri() {
        return baseUri;
    }
}
