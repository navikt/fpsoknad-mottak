package no.nav.foreldrepenger.mottak.domain;

import static no.nav.foreldrepenger.mottak.util.StringUtil.mask;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Joiner;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;

@Data
@JsonPropertyOrder({ "fornavn", "mellomnavn", "etternavn", "kjønn" })
public class Navn {

    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    @Exclude
    private final Kjønn kjønn;

    @JsonCreator
    public Navn(@JsonProperty("fornavn") String fornavn, @JsonProperty("mellomnavn") String mellomnavn,
            @JsonProperty("etternavn") String etternavn, @JsonProperty("kjønn") Kjønn kjønn) {
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        this.kjønn = kjønn;
    }

    public String navn() {
        return Joiner.on(' ').skipNulls().join(fornavn, mellomnavn, etternavn);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fornavn=" + fornavn + ", mellomnavn=" + mask(mellomnavn) + ", etternavn=" + mask(etternavn)
                + ", kjønn=" + kjønn + "]";
    }
}
