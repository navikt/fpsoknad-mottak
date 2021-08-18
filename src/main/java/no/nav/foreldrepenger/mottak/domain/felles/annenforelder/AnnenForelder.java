package no.nav.foreldrepenger.mottak.domain.felles.annenforelder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = UkjentForelder.class, name = "ukjent"),
        @Type(value = UtenlandskForelder.class, name = "utenlandsk"),
        @Type(value = NorskForelder.class, name = "norsk")
})
@Data
public abstract sealed class AnnenForelder permits NorskForelder,UtenlandskForelder,UkjentForelder {

    public abstract boolean hasId();

}
