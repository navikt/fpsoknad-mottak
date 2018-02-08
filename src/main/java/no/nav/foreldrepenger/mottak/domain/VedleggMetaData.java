package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final VedleggType type;
    private final Skjemanummer skjemanummer;

    public VedleggMetaData(Skjemanummer skjemanummer) {
        this(skjemanummer.dokumentTypeId(), VedleggType.PDF, skjemanummer);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse, @JsonProperty("type") VedleggType type,
            @JsonProperty("skjemanummer") Skjemanummer skjemanummer) {
        this.beskrivelse = beskrivelse;
        this.type = type;
        this.skjemanummer = skjemanummer;
    }

}
