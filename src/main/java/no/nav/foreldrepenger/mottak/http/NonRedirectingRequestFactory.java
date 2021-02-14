package no.nav.foreldrepenger.mottak.http;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

class NonRedirectingRequestFactory extends HttpComponentsClientHttpRequestFactory {
    public NonRedirectingRequestFactory() {
        setHttpClient(HttpClientBuilder.create()
                .disableRedirectHandling()
                .build());
    }
}