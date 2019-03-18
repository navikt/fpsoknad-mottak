package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedtakMetadata {

    private final String journalpostId;
    private final String versjon;

    @JsonCreator
    public VedtakMetadata(@JsonProperty("journalpostId") String journalpostId,
            @JsonProperty("versjon") String versjon) {
        this.journalpostId = journalpostId;
        this.versjon = versjon;
    }
}
