package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = UkjentForelder.class, name = "ukjent"),
        @Type(value = UtenlandskForelder.class, name = "utenlandsk"),
        @Type(value = NorskForelder.class, name = "norsk")
})
public abstract class AnnenForelder {

}
