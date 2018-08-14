package no.nav.foreldrepenger.mottak.domain.felles;

import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VedleggMetaData {

    private final String beskrivelse;
    private final String id;
    private final InnsendingsType innsendingsType;
    private final DokumentType dokumentType;

    public VedleggMetaData(String id, DokumentType dokumentType) {
        this(null, id, dokumentType);
    }

    public VedleggMetaData(String beskrivelse, String id, DokumentType dokumentType) {
        this(beskrivelse, id, LASTET_OPP, dokumentType);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse, @JsonProperty("id") String id,
            @JsonProperty("innsendingsType") InnsendingsType innsendingType,
            @JsonProperty("dokumentTyoe") DokumentType dokumentType) {
        this.id = id;
        this.beskrivelse = beskrivelse;
        this.innsendingsType = innsendingType;
        this.dokumentType = dokumentType;
    }
}
