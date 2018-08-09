package no.nav.foreldrepenger.mottak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonModulesConfiguration {

    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public Module jdk8Module() {
        return new Jdk8Module();
    }

    /*
     * @Bean public Module parameterNamesModule() { return new
     * ParameterNamesModule(PROPERTIES); }
     */

    @Bean
    public Module customSerializers() {
        return new CustomSerializerModule();
    }
}
