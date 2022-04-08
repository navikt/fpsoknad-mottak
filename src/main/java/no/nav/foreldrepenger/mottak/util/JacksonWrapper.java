package no.nav.foreldrepenger.mottak.util;

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

    public String writeValueAsString(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UnexpectedInputException(String.format("Kunne ikke serialisere fra %s",  object.getClass().getName()), e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + "]";
    }
}
