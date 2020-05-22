package no.nav.foreldrepenger.mottak.innsending.pdf.modell;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GruppeBlokk.class, name = "GRUPPE"),
    @JsonSubTypes.Type(value = FeltBlokk.class, name = "FELT"),
    @JsonSubTypes.Type(value = TabellBlokk.class, name = "TABELL"),
    @JsonSubTypes.Type(value = TabellRad.class, name = "TABELLRAD"),
    @JsonSubTypes.Type(value = FritekstBlokk.class, name = "FRITEKST")
})
public abstract class Blokk {
}
