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
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {
    private static final class MultipartMixedAwreFormMessageConverter extends FormHttpMessageConverter {
        @Override
        public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
            LOG.info("Types before {}", supportedMediaTypes);
            LOG.info("Setting supported mediatypes to multipart/mixed");
            super.setSupportedMediaTypes(
                    Collections.singletonList(MediaType.parseMediaType("multipart/mixed")));
        }

        @Override
        public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
            LOG.info("Checking if we can write  {} for {}", clazz, mediaType);
            if (!MultiValueMap.class.isAssignableFrom(clazz)) {
                return false;
            }
            if (mediaType == null || MediaType.ALL.equals(mediaType)) {
                return true;
            }
            /*
             * for (MediaType supportedMediaType : getSupportedMediaTypes()) { if
             * (supportedMediaType.isCompatibleWith(mediaType)) { return true; } }
             */
            return true;
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
        template.getMessageConverters().add(new MultipartMixedAwreFormMessageConverter());
        return template;
    }

}
