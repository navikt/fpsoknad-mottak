package no.nav.foreldrepenger.mottak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

@Configuration
class JacksonModulesConfiguration {

    @Bean
    public Module customSerializers() {
        return new CustomSerializerModule();
    }

}
