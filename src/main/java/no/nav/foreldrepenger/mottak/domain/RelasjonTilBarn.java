package no.nav.foreldrepenger.mottak.domain;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Valid
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = Fødsel.class, name = "fødsel"),
        @Type(value = Adopsjon.class, name = "adopsjon"),
        @Type(value = FremtidigFødsel.class, name = "termin"),
        @Type(value = Omsorgsovertakelse.class, name = "omsorgsovertakelse")
})
public abstract class RelasjonTilBarn {

    @Positive(message = "{ytelse.relasjontilbarn.antall}")
    private final int antallBarn;

    public RelasjonTilBarn(int antallBarn) {
        this.antallBarn = antallBarn;
    }

}
