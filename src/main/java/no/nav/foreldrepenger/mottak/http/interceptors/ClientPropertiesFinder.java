package no.nav.foreldrepenger.mottak.http.interceptors;

import org.springframework.http.HttpRequest;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;

public interface ClientPropertiesFinder {
    ClientProperties findProperties(ClientConfigurationProperties configs, HttpRequest request);
}
