package no.nav.foreldrepenger.mottak.domain;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

/*
<xsd:sequence>
<xsd:element minOccurs="0" name="mottattDato" type="xsd:date"/>
<xsd:element maxOccurs="unbounded" minOccurs="0" name="paakrevdeVedlegg" type="felles:Vedlegg"/>
<xsd:element minOccurs="0" name="begrunnelseForSenSoeknad" type="xsd:string"/>
<xsd:element minOccurs="0" name="tilleggsopplysninger" type="xsd:string"/>
<xsd:element name="omYtelse" type="felles:Ytelse"/>
<xsd:element name="soeker" type="felles:Bruker"/>
<xsd:element minOccurs="0" name="soeknadsvariant" type="felles:Soeknadsvarianter"/>
<xsd:element maxOccurs="unbounded" minOccurs="0" name="andreVedlegg" type="felles:Vedlegg"/>
</xsd:sequence>
*/

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = Engangsstønad.class, name = "engangsstønad")
})
public class Søknad {

    private final LocalDate søknadsdato;
    private final Søker søker;
    private final Ytelse ytelse;

    @ConstructorProperties({ "søknadsdato", "søker", "ytelse" })
    public Søknad(LocalDate søknadsdato, Søker søker, Ytelse ytelse) {
        this.søknadsdato = søknadsdato;
        this.søker = søker;
        this.ytelse = ytelse;
    }
}
