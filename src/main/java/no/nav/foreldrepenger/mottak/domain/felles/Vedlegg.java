package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.SEND_SENERE;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = ValgfrittVedlegg.class, name = "valgfritt"),
        @Type(value = PåkrevdVedlegg.class, name = "påkrevd")
})
public abstract class Vedlegg {

    private static final Logger LOG = LoggerFactory.getLogger(Vedlegg.class);

    private final VedleggMetaData metadata;
    private final byte[] vedlegg;

    @JsonCreator
    public Vedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg) {
        this.metadata = metadata;
        this.vedlegg = vedlegg;
    }

    @JsonIgnore
    public String getBeskrivelse() {
        return metadata.getBeskrivelse();
    }

    @JsonIgnore
    public InnsendingsType getInnsendingsType() {
        if (getStørrelse() == 0) {
            LOG.info("Ingen vedlegg, setter type til SEND_SENERE siden vi ikke haar bytes for vedlegg");
            return SEND_SENERE;
        }
        if (metadata.getInnsendingsType() == null) {
            LOG.info("Ingen innsendingstype er satt, setter type til LASTET_OPP, siden vi har bytes for vedlegg");
            return LASTET_OPP;
        }
        return metadata.getInnsendingsType();
    }

    @JsonIgnore
    public String getId() {
        return metadata.getId();
    }

    @JsonIgnore
    public DokumentType getDokumentType() {
        return metadata.getDokumentType();
    }

    @JsonIgnore
    public long getStørrelse() {
        return vedlegg == null ? 0 : vedlegg.length;
    }

    private String bytes() {
        String vedleggAsString = Arrays.toString(vedlegg);
        if (vedleggAsString.length() >= 50) {
            return vedleggAsString.substring(0, 49) + ".... " + (vedleggAsString.length() - 50) + " more bytes";
        }
        return vedleggAsString;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "metadata=" + metadata + "vedlegg=" + bytes();
    }
}
