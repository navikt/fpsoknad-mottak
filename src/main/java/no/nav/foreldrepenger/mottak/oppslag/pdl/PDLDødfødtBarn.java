package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

record PDLDødfødtBarn(@JsonProperty("dato") LocalDate dato) {
}
