package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = Engangsstønad.class, name = "engangsstønad"),
        @Type(value = Foreldrepenger.class, name = "foreldrepenger"),
        @Type(value = Svangerskapspenger.class, name = "svangerskapspenger")
})
@Data
@Valid
public abstract class Ytelse {

}
