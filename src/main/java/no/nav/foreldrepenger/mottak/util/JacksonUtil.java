package no.nav.foreldrepenger.mottak.util;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;

@Component
public class JacksonUtil {

    private final ObjectMapper mapper;

    public JacksonUtil(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T convertTo(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new UnexpectedInputException("Kunne ikke rekonstuere melding fra %s til %s", e, json, clazz);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + "]";
    }

    public String writeValueAsString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UnexpectedInputException("Kunne ikke serialisere fra %s", e, object.getClass().getName());

        }
    }
}
