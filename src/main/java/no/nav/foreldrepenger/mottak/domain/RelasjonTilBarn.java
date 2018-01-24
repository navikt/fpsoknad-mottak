package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = Fødsel.class, name = "fødsel"),
        @Type(value = Adopsjon.class, name = "adopsjon"),
        @Type(value = Omsorgsovertakelse.class, name = "omsorgsovertakelse")
})
public abstract class RelasjonTilBarn {

    private final int antallBarn;

    public RelasjonTilBarn(int antallBarn) {
        this.antallBarn = antallBarn;
    }

}
