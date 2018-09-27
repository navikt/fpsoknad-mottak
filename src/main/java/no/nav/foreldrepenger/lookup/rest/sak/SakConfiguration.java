package no.nav.foreldrepenger.lookup.rest.sak;

import no.nav.security.spring.oidc.validation.interceptor.BearerTokenClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
public class SakConfiguration {

    @Value("${SAK_SAKER_URL")
    private String sakBaseUrl;

    @Value("${SECURITYTOKENSERVICE_URL}")
    private String stsUrl;

    @Value("${FPSELVBETJENING_USERNAME}")
    private String serviceUser;

    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

    @Bean
    public SakClientHttp sakClient(RestTemplate restTemplate, StsClient stsClient) {
        return new SakClientHttp(sakBaseUrl, restTemplate, stsClient);
    }

    @Bean
    public RestTemplate restTemplateSak(ClientHttpRequestInterceptor... interceptors) {
        // We'll add our own auth header with SAML elsewhere
        BearerTokenClientHttpRequestInterceptor[] interceptorsExceptAuth =
            Arrays.stream(interceptors)
                .filter(i -> !(i instanceof BearerTokenClientHttpRequestInterceptor))
                .toArray(BearerTokenClientHttpRequestInterceptor[]::new);

        return new RestTemplateBuilder()
            .interceptors(interceptorsExceptAuth)
            .build();
    }

    @Bean
    public StsClient stsClient(RestTemplate restTemplate) {
        return new StsClient(restTemplate, stsUrl, serviceUser, servicePwd);
    }

}
