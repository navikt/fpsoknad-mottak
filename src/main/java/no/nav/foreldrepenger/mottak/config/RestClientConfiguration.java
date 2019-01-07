package no.nav.foreldrepenger.mottak.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter;
import no.nav.foreldrepenger.mottak.http.NonRedirectingRequestFactory;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestOperations restTemplate(ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(interceptors)
                .build();
        template.getMessageConverters().add(new MultipartMixedAwareMessageConverter());
        return template;
    }

}
