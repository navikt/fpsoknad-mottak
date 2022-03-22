package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ArbeidsgiverType {
    @JsonProperty("Organisasjon")
    ORGANISASJON,
    @JsonProperty("Person")
    PERSON
}
