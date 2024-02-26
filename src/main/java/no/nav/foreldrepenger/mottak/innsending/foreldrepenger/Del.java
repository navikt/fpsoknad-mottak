package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.common.domain.felles.DokumentType;

@JsonPropertyOrder({ "contentId", "dokumentTypeId" })
class Del {

    private static final Logger LOG = LoggerFactory.getLogger(Del.class);

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

    @JsonCreator
    public Del(@JsonProperty("dokumentType") DokumentType dokumentType,
            @JsonProperty("contentId") int contentId) {
        this.dokumentTypeId = dokumentType.name();
        this.contentId = String.valueOf(contentId);
        LOG.debug("La til del {} med id {} ({})", dokumentType.name(), contentId, dokumentType.getTittel());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokumentTypeId=" + dokumentTypeId + ", contentId=" + contentId + "]";
    }
}
