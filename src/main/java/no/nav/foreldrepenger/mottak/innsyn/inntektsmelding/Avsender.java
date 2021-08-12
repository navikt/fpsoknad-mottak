package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.time.LocalDateTime;

record Avsender(String navn, String versjon, LocalDateTime tidspunkt) {
}
