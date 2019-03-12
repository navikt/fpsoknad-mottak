package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Orgnr;

@Data
@EqualsAndHashCode(callSuper = true)
public class Virksomhet extends Arbeidsforhold {

    @Orgnr
    public final String orgnr;

    @JsonCreator
    public Virksomhet(@JsonProperty("orgnr") String orgnr) {
        this.orgnr = orgnr;
    }

}
