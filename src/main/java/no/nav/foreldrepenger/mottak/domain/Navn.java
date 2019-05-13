package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.felles.Kjønn;

@Data
@JsonPropertyOrder({ "fornavn", "mellomnavn", "etternavn" })
public class Navn {

    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final Kjønn kjønn;

    @JsonCreator
    public Navn(@JsonProperty("fornavn") String fornavn, @JsonProperty("mellomnavn") String mellomnavn,
            @JsonProperty("etternavn") String etternavn,@JsonProperty("kjønn") Kjønn kjønn) {
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        this.kjønn = kjønn;
    }
}
