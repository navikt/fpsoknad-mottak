package no.nav.foreldrepenger.mottak.util;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.error.UnexpectedInputException;

@Component
public class JacksonWrapper {

    private final ObjectMapper mapper;

    public JacksonWrapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T readValue(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new UnexpectedInputException("Kunne ikke rekonstuere melding fra %s til %s", e, json, clazz);
        }
    }

    public String writeValueAsString(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UnexpectedInputException("Kunne ikke serialisere fra %s", e, object.getClass().getName());

        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + "]";
    }
}
