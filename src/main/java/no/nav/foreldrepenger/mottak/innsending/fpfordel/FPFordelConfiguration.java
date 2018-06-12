package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        FormHttpMessageConverter cv = new FormHttpMessageConverter() {

            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                if (clazz == LinkedMultiValueMap.class) {
                    return true;
                }
                return super.canWrite(clazz, mediaType);
            }

            @Override
            public boolean canRead(Class<?> clazz, MediaType mediaType) {
                if (clazz == LinkedMultiValueMap.class) {
                    return true;
                }
                return super.canRead(clazz, mediaType);
            }
        };
        RestTemplate jalla = new RestTemplateBuilder()
                .rootUri(cfg.getUri())
                .interceptors(interceptors)
                // .messageConverters(cv)
                .build();

        jalla.getMessageConverters().add(cv);
        return jalla;
    }
}
