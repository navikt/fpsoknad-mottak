package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.Valid;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@Valid
@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Virksomhet.class, name = "virksomhet"),
    @JsonSubTypes.Type(value = PrivatArbeidsgiver.class, name = "privat"),
    @JsonSubTypes.Type(value = SelvstendigNæringsdrivende.class, name = "selvstendig"),
    @JsonSubTypes.Type(value = Frilanser.class, name = "frilanser")
})
public abstract class Arbeidsforhold {
}
