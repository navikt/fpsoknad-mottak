package no.nav.foreldrepenger.mottak.oppslag.kontonummer;

import no.nav.foreldrepenger.mottak.oppslag.AbstractConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

import static no.nav.foreldrepenger.mottak.util.URIUtil.uri;

/**
 * Swagger: https://sokos-kontoregister-person.dev.intern.nav.no/api/borger/v1/docs/#/kontoregister.v1/kontooppslag%20med%20get
 */
@ConfigurationProperties(prefix = "kontoregister")
public class KontoregisterConfig extends AbstractConfig {
    private static final String DEFAULT_PING_PATH = "/";
    private static final String DEFAULT_URI = "https://sokos-kontoregister-person.intern.nav.no";
    private static final String DEFAULT_BASE_PATH = "api/borger/v1/hent-aktiv-konto";

    public KontoregisterConfig(@DefaultValue(DEFAULT_URI) URI baseUri,
                               @DefaultValue(DEFAULT_PING_PATH) String pingPath,
                               @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);
    }

    URI kontoregisterURI() {
        return uri(getBaseUri(), DEFAULT_BASE_PATH);
    }
}
