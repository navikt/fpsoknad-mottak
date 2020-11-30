package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

record PDLDødsfall(@JsonProperty("doedsdato") LocalDate dødsdato) {
}
