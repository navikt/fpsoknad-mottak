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
        this(dokumentType.beskrivelse, id, LASTET_OPP, dokumentType);
    }

    @JsonCreator
    public VedleggMetaData(@JsonProperty("beskrivelse") String beskrivelse, @JsonProperty("id") String id,
            @JsonProperty("innsendingsType") InnsendingsType innsendingType,
            @JsonProperty("dokumentType") DokumentType dokumentType) {
        this.beskrivelse = beskrivelse != null ? beskrivelse : dokumentType.beskrivelse;
        this.id = id;
        this.innsendingsType = innsendingType;
        this.dokumentType = dokumentType;
    }
}
