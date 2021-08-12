package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.time.LocalDate;


record Naturalytelse(NaturalytelseType type, Double beløpPrMåned, LocalDate fom) {

}
