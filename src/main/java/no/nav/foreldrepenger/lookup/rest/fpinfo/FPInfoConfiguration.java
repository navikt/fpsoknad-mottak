package no.nav.foreldrepenger.lookup.rest.fpinfo;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPInfoConfiguration {
    @Bean
    public RestTemplate restTemplate(FPInfoConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getBaseURL())
                .interceptors(interceptors)
                .errorHandler(new FPInfoResponseErrorHandler())
                .build();
        return template;
    }

}
