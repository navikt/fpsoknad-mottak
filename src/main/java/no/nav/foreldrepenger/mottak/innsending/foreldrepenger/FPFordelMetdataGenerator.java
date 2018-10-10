package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FPFordelMetdataGenerator {

    private final ObjectMapper mapper;

    public FPFordelMetdataGenerator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String generateMetadata(FPFordelMetadata metadata) {
        return generateMetadata(metadata, true);
    }

    String generateMetadata(FPFordelMetadata metadata, boolean pretty) {
        try {
            return pretty ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata)
                    : mapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + "]";
    }
}
