package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {
        return new RestTemplateBuilder()
                .rootUri(cfg.getUri())
                .interceptors(interceptors)
                .messageConverters(new MappingJackson2HttpMessageConverter(), new FormHttpMessageConverter())
                .build();
    }
}
