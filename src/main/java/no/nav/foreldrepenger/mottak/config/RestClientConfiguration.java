package no.nav.foreldrepenger.mottak.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.http.MultipartMixedAwareMessageConverter;
import no.nav.foreldrepenger.mottak.http.NonRedirectingRequestFactory;
import no.nav.foreldrepenger.mottak.http.errorhandling.RestClientResponseErrorHandler;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelConfig;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getUri().toString())
                .requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(interceptors)
                .errorHandler(new RestClientResponseErrorHandler())
                .build();
        template.getMessageConverters().add(new MultipartMixedAwareMessageConverter());
        return template;
    }

}
