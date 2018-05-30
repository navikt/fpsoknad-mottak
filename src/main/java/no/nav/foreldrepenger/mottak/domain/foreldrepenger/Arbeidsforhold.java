package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = NorskArbeidsforhold.class, name = "norsk"),
        @Type(value = UtenlandskArbeidsforhold.class, name = "utenlandsk")
})
public abstract class Arbeidsforhold {
    private final String arbeidsgiverNavn;
    private final String beskrivelseRelasjon;
    private final ÅpenPeriode periode;
    private final List<String> vedlegg;
}
