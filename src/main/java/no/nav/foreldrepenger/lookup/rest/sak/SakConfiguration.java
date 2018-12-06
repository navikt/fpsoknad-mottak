package no.nav.foreldrepenger.lookup.rest.sak;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.rest.StatusCodeConvertingResponseErrorHandler;
import no.nav.security.spring.oidc.validation.interceptor.BearerTokenClientHttpRequestInterceptor;

@Configuration
public class SakConfiguration {

    @Value("${SAK_SAKER_URL}")
    private String sakBaseUrl;

    @Value("${SECURITYTOKENSERVICE_URL}")
    private String stsUrl;

    @Value("${FPSELVBETJENING_USERNAME}")
    private String serviceUser;

    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

    @Bean
    public SakClientHttp sakClient(RestOperations restOperations, StsClient stsClient) {
        return new SakClientHttp(sakBaseUrl, restOperations, stsClient);
    }

    @Bean
    public RestOperations restTemplateSak(TokenHandler tokenHandler, ClientHttpRequestInterceptor... interceptors) {
        List<ClientHttpRequestInterceptor> interceptorListWithoutAuth = Arrays.stream(interceptors)
                // We'll add our own auth header with SAML elsewhere
                .filter(i -> !(i instanceof BearerTokenClientHttpRequestInterceptor))
                .collect(toCollection(ArrayList::new));

        ClientHttpRequestInterceptor[] interceptorsAsArray = interceptorListWithoutAuth.stream()
                .toArray(ClientHttpRequestInterceptor[]::new);

        return new RestTemplateBuilder()
                .interceptors(interceptorsAsArray)
                .errorHandler(new StatusCodeConvertingResponseErrorHandler(tokenHandler))
                .build();
    }

    @Bean
    public StsClient stsClient(RestOperations restTemplate) {
        return new StsClient(restTemplate, stsUrl, serviceUser, servicePwd);
    }

}
