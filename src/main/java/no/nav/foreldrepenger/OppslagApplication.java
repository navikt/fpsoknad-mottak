package no.nav.foreldrepenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoConfig;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoResponseErrorHandler;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplication.class, args);
    }

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
