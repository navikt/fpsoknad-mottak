package no.nav.foreldrepenger.mottak.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.Module;

import no.nav.foreldrepenger.common.domain.serialization.CustomSerializerModule;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;

@Configuration
class JacksonConfiguration {

    @Bean
    public Module customSerializers() {
        return new CustomSerializerModule();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return b -> b.mixIn(OAuth2AccessTokenResponse.class, IgnoreUnknownMixin.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface IgnoreUnknownMixin {

    }
}
