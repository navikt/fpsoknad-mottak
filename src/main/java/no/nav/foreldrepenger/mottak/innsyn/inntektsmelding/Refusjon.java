package no.nav.foreldrepenger.mottak.innsyn.inntektsmelding;

import java.time.LocalDate;
import java.util.List;

record Refusjon(Double beløpPrMåned,
      LocalDate opphørsDato,
      List<RefusjonsEndring> refusjonsEndringer) {

}
