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
    @JsonSubTypes.Type(value = TemaBlokk.class, name = "TEMA"),
    @JsonSubTypes.Type(value = FeltBlokk.class, name = "FELT"),
    @JsonSubTypes.Type(value = GruppeBlokk.class, name = "GRUPPE"),
    @JsonSubTypes.Type(value = TabellRad.class, name = "TABELLRAD"),
    @JsonSubTypes.Type(value = FritekstBlokk.class, name = "FRITEKST"),
    @JsonSubTypes.Type(value = ListeBlokk.class, name = "LISTE")
})
public abstract class Blokk {
}
