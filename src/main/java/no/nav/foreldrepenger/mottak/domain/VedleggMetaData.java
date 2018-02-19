package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final Filtype type;
    private final Skjemanummer skjemanummer;

    public VedleggMetaData(Skjemanummer skjemanummer) {
        this(skjemanummer.dokumentTypeId(), Filtype.PDF, skjemanummer);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse, @JsonProperty("type") Filtype type,
            @JsonProperty("skjemanummer") Skjemanummer skjemanummer) {
        this.beskrivelse = beskrivelse;
        this.type = type;
        this.skjemanummer = skjemanummer;
    }

}
