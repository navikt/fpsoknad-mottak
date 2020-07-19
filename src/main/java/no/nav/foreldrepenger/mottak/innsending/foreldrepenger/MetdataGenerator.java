package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.util.JacksonWrapper;

@Component
public class MetdataGenerator {

    private final JacksonWrapper mapper;

    public MetdataGenerator(JacksonWrapper mapper) {
        this.mapper = mapper;
    }

    public String generer(FordelMetadata metadata) {
            return mapper.writeValueAsString(metadata);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapper=" + mapper + "]";
    }
}
