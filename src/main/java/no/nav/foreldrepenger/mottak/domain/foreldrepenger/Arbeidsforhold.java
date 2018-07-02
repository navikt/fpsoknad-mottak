package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = UtenlandskArbeidsforhold.class, name = "utenlandsk")
})
public abstract class Arbeidsforhold {
    @Length(max = 50)
    private final String arbeidsgiverNavn;
    private final String beskrivelseRelasjon;
    private final ÅpenPeriode periode;
    private final List<String> vedlegg;

    @JsonCreator
    public Arbeidsforhold(@JsonProperty("arbeidsgiverNavn") String arbeidsgiverNavn,
            @JsonProperty("beskrivelseRelasjon") String beskrivelseRelasjon,
            @JsonProperty("periode") ÅpenPeriode periode,
            @JsonProperty("vedlegg") List<String> vedlegg) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
        this.beskrivelseRelasjon = beskrivelseRelasjon;
        this.periode = periode;
        this.vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());

    }
}
