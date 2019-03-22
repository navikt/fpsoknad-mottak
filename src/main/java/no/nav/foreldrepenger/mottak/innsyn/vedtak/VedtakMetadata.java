package no.nav.foreldrepenger.mottak.innsyn.vedtak;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.FagsakType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Data
public class VedtakMetadata {

    private final String journalpostId;
    private final String versjon;
    private final FagsakType type;

    @JsonCreator
    public VedtakMetadata(@JsonProperty("journalpostId") String journalpostId,
            @JsonProperty("egenskap") SøknadEgenskap e) {
        this.journalpostId = journalpostId;
        this.versjon = e.getVersjon().name();
        this.type = e.getFagsakType();
    }
}
