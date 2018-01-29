package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/*
<xsd:sequence>
<xsd:element minOccurs="0" name="soeknadsvariant" type="felles:Soeknadsvarianter"/>
</xsd:sequence>
*/

@Data
public class Søknad {
    @NotNull
    private final LocalDate motattdato;
    @Valid
    private final Søker søker;
    @Valid
    private final Engangsstønad ytelse;
    private String begrunnelseForSenSøknad;
    private String tilleggsopplysninger;
    private final List<Vedlegg> påkrevdeVedlegg;
    private final List<Vedlegg> frivilligeVedlegg;

    public Søknad(@JsonProperty("motattdato") LocalDate motattdato, @JsonProperty("søker") Søker søker,
            @JsonProperty("ytelse") Engangsstønad ytelse) {
        this(motattdato, søker, ytelse, Collections.emptyList(), Collections.emptyList());
    }

    @JsonCreator
    public Søknad(@JsonProperty("motattdato") LocalDate motattdato, @JsonProperty("søker") Søker søker,
            @JsonProperty("ytelse") Engangsstønad ytelse,
            @JsonProperty("påkrevdeVedlegg") List<Vedlegg> påkrevdeVedlegg,
            @JsonProperty("frivilligeVedlegg") List<Vedlegg> frivilligeVedlegg) {
        this.motattdato = motattdato;
        this.søker = søker;
        this.ytelse = ytelse;
        this.påkrevdeVedlegg = påkrevdeVedlegg == null ? Collections.emptyList() : påkrevdeVedlegg;
        this.frivilligeVedlegg = frivilligeVedlegg == null ? Collections.emptyList() : frivilligeVedlegg;

    }
}
