package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Data
public class VedtakMetadata {

    private final String journalpostId;
    private final Versjon versjon;

    @JsonCreator
    public VedtakMetadata(@JsonProperty("journalpostId") String journalpostId,
            @JsonProperty("versjon") Versjon versjon) {
        this.journalpostId = journalpostId;
        this.versjon = versjon;
    }
}
