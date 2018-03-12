package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final Filtype type;
    private final VedleggSkjemanummer skjemanummer;

    public VedleggMetaData(VedleggSkjemanummer skjemanummer) {
        this(skjemanummer.id, skjemanummer);
    }

    public VedleggMetaData(String beskrivelse, VedleggSkjemanummer skjemanummer) {
        this(beskrivelse, Filtype.PDF, skjemanummer);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse, @JsonProperty("type") Filtype type,
            @JsonProperty("skjemanummer") VedleggSkjemanummer skjemanummer) {
        this.beskrivelse = beskrivelse;
        this.type = type;
        this.skjemanummer = skjemanummer;
    }

}
