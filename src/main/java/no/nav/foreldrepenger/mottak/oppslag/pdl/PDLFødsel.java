package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

record PDLFødsel(@JsonProperty("foedselsdato") LocalDate fødselsdato) {
}