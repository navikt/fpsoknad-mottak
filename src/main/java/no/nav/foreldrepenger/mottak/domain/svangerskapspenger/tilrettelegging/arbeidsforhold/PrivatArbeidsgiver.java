package no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

@Data
@EqualsAndHashCode(callSuper = false)
public class PrivatArbeidsgiver extends Arbeidsforhold {

    @NotNull
    public final Fødselsnummer fnr;

    public PrivatArbeidsgiver(String fnr) {
        this(new Fødselsnummer(fnr));
    }

    @JsonCreator
    public PrivatArbeidsgiver(@JsonProperty("fnr") Fødselsnummer fnr) {
        this.fnr = fnr;
    }
}
