package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Valid
@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = Fødsel.class, name = "fødsel"),
        @Type(value = Adopsjon.class, name = "adopsjon"),
        @Type(value = FremtidigFødsel.class, name = "termin"),
        @Type(value = Omsorgsovertakelse.class, name = "omsorgsovertakelse")
})
public abstract class RelasjonTilBarn {
    public abstract LocalDate relasjonsDato();

    @Positive(message = "{ytelse.relasjontilbarn.antall}")
    @Max(10)
    private final int antallBarn;

    public RelasjonTilBarn(int antallBarn) {
        this.antallBarn = antallBarn;
    }
}
