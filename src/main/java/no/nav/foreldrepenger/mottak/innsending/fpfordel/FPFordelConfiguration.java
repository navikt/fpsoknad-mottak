package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {
    private static final class Converter extends FormHttpMessageConverter {
        @Override
        public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
            LOG.info("Types before {}", supportedMediaTypes);
            LOG.info("Setting supported mediatypes to multipart/mixed");
            super.setSupportedMediaTypes(
                    Collections.singletonList(MediaType.parseMediaType("multipart/mixed")));
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelConfiguration.class);

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getUri())
                .interceptors(interceptors)
                .build();
        LOG.info("Adding converter for multipart/mixed");
        template.getMessageConverters().add(new Converter());
        return template;
    }

}
