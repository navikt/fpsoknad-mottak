package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;

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

    @JsonPropertyOrder({ "contentId", "dokumentTypeId" })
    static class Filer {

        private final String dokumentTypeId;
        private final String contentId;

        @JsonProperty("Content-ID")
        public String getContentId() {
            return contentId;
        }

        @JsonProperty("dokumentTypeId")
        public String getDokumentTypeId() {
            return dokumentTypeId;
        }

        public Filer(DokumentType dokumentType, int contentId) {
            this.dokumentTypeId = dokumentType.name();
            this.contentId = String.valueOf(contentId);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [dokumentTypeId=" + dokumentTypeId + ", contentId=" + contentId + "]";
        }
    }
}
