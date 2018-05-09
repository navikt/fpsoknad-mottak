package no.nav.foreldrepenger.mottak.fpfordel;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {
        return new RestTemplateBuilder()
                .rootUri(cfg.getFordelUrl())
                .interceptors(interceptors)
                .build();
    }
}
