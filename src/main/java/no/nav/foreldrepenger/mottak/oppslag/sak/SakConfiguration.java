package no.nav.foreldrepenger.mottak.oppslag.sak;

import static java.util.stream.Collectors.toCollection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.mottak.http.interceptors.TokenExchangeClientRequestInterceptor;
import no.nav.foreldrepenger.mottak.util.TokenUtil;
import no.nav.security.token.support.spring.validation.interceptor.BearerTokenClientHttpRequestInterceptor;

@Configuration
public class SakConfiguration {

    static final String SAK = "sak";

    @Value("${sak.saker.url}")
    private URI sakBaseUrl;

    @Value("${sak.securitytokenservice.url}")
    private URI stsUrl;

    @Value("${sak.securitytokenservice.username}")
    private String serviceUser;

    @Value("${sak.securitytokenservice.password}")
    private String servicePwd;

    @Bean
    @Qualifier(SAK)
    public RestOperations restOperationsSak(ClientHttpRequestInterceptor... interceptors) {
        List<ClientHttpRequestInterceptor> interceptorListWithoutAuth = Arrays.stream(interceptors)
                .filter(i -> !(i instanceof BearerTokenClientHttpRequestInterceptor))
                .filter(i -> !(i instanceof TokenExchangeClientRequestInterceptor))
                .collect(toCollection(ArrayList::new));

        return new RestTemplateBuilder()
                .interceptors(interceptorListWithoutAuth.toArray(ClientHttpRequestInterceptor[]::new))
                .build();

    }

    @Bean
    public StsClient stsClient(RestOperations restOperations) {
        return new StsClientHttp(restOperations, stsUrl, serviceUser, servicePwd);
    }

    @Bean
    public SakClient sakClient(@Qualifier(SAK) RestOperations restOperations, StsClient stsClient, TokenUtil tokenUtil) {
        return new SakClientHttp(sakBaseUrl, restOperations, stsClient, tokenUtil);
    }
}
