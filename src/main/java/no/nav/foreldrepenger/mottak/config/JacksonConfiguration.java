package no.nav.foreldrepenger.mottak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.mapper.DefaultJsonMapper;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;

@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public ObjectMapper customObjectmapper() {
        return DefaultJsonMapper.MAPPER
            .addMixIn(OAuth2AccessTokenResponse.class, IgnoreUnknownMixin.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface IgnoreUnknownMixin {

    }
}
