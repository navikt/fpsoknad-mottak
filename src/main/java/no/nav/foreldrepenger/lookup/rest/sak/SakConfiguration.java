package no.nav.foreldrepenger.lookup.rest.sak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SakConfiguration {

    @Value("${SAK_SAKER_URL")
    private String sakBaseUrl;

    @Bean
    public SakClientHttp sakClient() {
        return new SakClientHttp(sakBaseUrl, restTemplate());
    }

    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
            .rootUri(sakBaseUrl)
            .build();
    }
}
