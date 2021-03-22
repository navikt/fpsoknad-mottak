package no.nav.foreldrepenger.mottak.http.interceptors;

import java.net.URI;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;

public interface ClientPropertiesFinder {

    default ClientProperties findProperties(ClientConfigurationProperties configs, URI uri) {
        return configs.getRegistration().get(uri.getHost());
    }

}
