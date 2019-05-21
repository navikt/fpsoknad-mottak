package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.LASTET_OPP;
import static no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType.SEND_SENERE;
import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;
import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = { "vedlegg" })

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = ValgfrittVedlegg.class, name = "valgfritt"),
        @Type(value = PåkrevdVedlegg.class, name = "påkrevd")
})
public abstract class Vedlegg {

    private static final Logger LOG = LoggerFactory.getLogger(Vedlegg.class);

    private final VedleggMetaData metadata;
    protected final byte[] vedlegg;

    @JsonCreator
    public Vedlegg(@JsonProperty("metadata") VedleggMetaData metadata, @JsonProperty("vedlegg") byte[] vedlegg) {
        this.metadata = metadata;
        this.vedlegg = vedlegg;
    }

    @JsonIgnore
    public String getBeskrivelse() {
        return Optional.ofNullable(metadata.getBeskrivelse())
                .orElse(getDokumentType().beskrivelse);
    }

    @JsonIgnore
    public InnsendingsType getInnsendingsType() {
        InnsendingsType type = metadata.getInnsendingsType();
        if (getStørrelse() == 0) {
            if (!SEND_SENERE.equals(type) && type != null) {
                LOG.warn("Feil innsendingstype {} for {}, ingen vedlegg, setter type til SEND_SENERE", type,
                        metadata.getDokumentType());
            }
            return SEND_SENERE;
        }
        if (type == null) {
            LOG.info(
                    "Innsendingstype for {} er ikke satt, setter til LASTET_OPP, siden vi har vedlegg med størrelse {}",
                    metadata.getDokumentType(), getStørrelse());
            return LASTET_OPP;
        }
        return type;
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
        return Optional.ofNullable(vedlegg)
                .map(v -> v.length)
                .orElse(0);
    }

    protected static byte[] bytesFra(Resource vedlegg) {
        try (InputStream is = vedlegg.getInputStream()) {
            return copyToByteArray(is);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "metadata=" + metadata + "vedlegg=" + limit(vedlegg, 50);
    }
}
