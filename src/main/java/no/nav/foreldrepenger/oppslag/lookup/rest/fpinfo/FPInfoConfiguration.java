package no.nav.foreldrepenger.oppslag.lookup.rest.fpinfo;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configurable
public class FPInfoConfiguration {
    @Bean
    public RestTemplate restTemplate(FPInfoConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getFpinfo())
                .interceptors(interceptors)
                .errorHandler(new FPInfoResponseErrorHandler())
                .build();
        return template;
    }

}
