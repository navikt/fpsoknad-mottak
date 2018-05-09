package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final VedleggSkjemanummer skjemanummer;

    public VedleggMetaData(VedleggSkjemanummer skjemanummer) {
        this(skjemanummer.id, skjemanummer);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse,
            @JsonProperty("skjemanummer") VedleggSkjemanummer skjemanummer) {
        this.beskrivelse = beskrivelse;
        this.skjemanummer = skjemanummer;
    }
}
