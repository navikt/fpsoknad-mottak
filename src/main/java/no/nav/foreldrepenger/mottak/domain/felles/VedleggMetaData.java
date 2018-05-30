package no.nav.foreldrepenger.mottak.domain.felles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final VedleggSkjemanummer skjemanummer;
    private final String id;

    public VedleggMetaData(VedleggSkjemanummer skjemanummer) {
        this(skjemanummer.id, skjemanummer);
    }

    public VedleggMetaData(String beskrivelse, VedleggSkjemanummer skjemanummer) {
        this(beskrivelse, skjemanummer, null);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse,
            @JsonProperty("skjemanummer") VedleggSkjemanummer skjemanummer, @JsonProperty("id") String id) {
        this.beskrivelse = beskrivelse;
        this.skjemanummer = skjemanummer;
        this.id = id;
    }
}
