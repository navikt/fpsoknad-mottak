package no.nav.foreldrepenger.mottak.oppslag.sak;

import static java.util.stream.Collectors.toCollection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.util.TokenUtil;
import no.nav.security.token.support.spring.validation.interceptor.BearerTokenClientHttpRequestInterceptor;

@Configuration
public class SakConfiguration {

    @Value("${sak.saker.url}")
    private URI sakBaseUrl;

    @Value("${sak.securitytokenservice.url}")
    private URI stsUrl;

    @Value("${sak.securitytokenservice.username}")
    private String serviceUser;

    @Value("${sak.securitytokenservice.password}")
    private String servicePwd;

    private static final Logger LOG = LoggerFactory.getLogger(SakConfiguration.class);

    @Bean
    @Qualifier("sak123")
    public RestTemplate restOperationsSak(ClientHttpRequestInterceptor... interceptors) {
        List<ClientHttpRequestInterceptor> interceptorListWithoutAuth = Arrays.stream(interceptors)
                // We'll add our own auth header with SAML elsewhere
                .filter(i -> !(i instanceof BearerTokenClientHttpRequestInterceptor))
                .collect(toCollection(ArrayList::new));

        var ops = new RestTemplateBuilder()
                .interceptors(interceptorListWithoutAuth.stream()
                        .toArray(ClientHttpRequestInterceptor[]::new))
                .build();
        LOG.info("Interceptors konfig" + ops.getInterceptors());
        return ops;

    }

    @Bean
    public StsClient stsClient(RestOperations restOperations) {
        return new StsClientHttp(restOperations, stsUrl, serviceUser, servicePwd);
    }

    @Bean
    public SakClient sakClient(@Qualifier("sak123") RestTemplate restOperations, StsClient stsClient, TokenUtil tokenUtil) {
        return new SakClientHttp(sakBaseUrl, restOperations, stsClient, tokenUtil);
    }
}
