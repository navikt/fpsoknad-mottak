package no.nav.foreldrepenger.mottak.http;

import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class TokenClientRestTemplateConfiguration {

    private static final Duration READ_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    // Konfigurererer timeouts for DefaultOAuth2HttpClient fra token-client-spring
    @Bean
    public RestTemplateBuilder resttemplateBuilderCustomizer(RestTemplateBuilderConfigurer configurer) {
        return configurer.configure(new RestTemplateBuilder())
            .setReadTimeout(READ_TIMEOUT)
            .setConnectTimeout(CONNECT_TIMEOUT);
    }
}
