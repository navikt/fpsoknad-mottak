package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getUri())
                .interceptors(interceptors)
                .errorHandler(new FPFordeResponseErrorHandler())
                .build();
        template.getMessageConverters().add(new MultipartMixedAwreMessageConverter());
        return template;
    }
}
