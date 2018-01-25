package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/*
<xsd:sequence>
<xsd:element maxOccurs="unbounded" minOccurs="0" name="paakrevdeVedlegg" type="felles:Vedlegg"/>
<xsd:element minOccurs="0" name="begrunnelseForSenSoeknad" type="xsd:string"/>
<xsd:element minOccurs="0" name="soeknadsvariant" type="felles:Soeknadsvarianter"/>
<xsd:element maxOccurs="unbounded" minOccurs="0" name="andreVedlegg" type="felles:Vedlegg"/>
</xsd:sequence>
*/

@Data
public class Søknad {
    private final LocalDate motattdato;
    private final Søker søker;
    private final Engangsstønad ytelse;
    private String begrunnelseForSenSøknad;
    private String tilleggsopplysninger;

    @JsonCreator
    public Søknad(@JsonProperty("motattdato") LocalDate motattdato, @JsonProperty("søker") Søker søker,
            @JsonProperty("ytelse") Engangsstønad ytelse) {
        this.motattdato = motattdato;
        this.søker = søker;
        this.ytelse = ytelse;
    }
}
