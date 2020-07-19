package no.nav.foreldrepenger.mottak.config;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@Configuration
class JacksonModulesConfiguration {

    @Bean
    public Module customSerializers() {
        return new CustomSerializerModule();
    }

}
