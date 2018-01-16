package no.nav.foreldrepenger.oppslag.inntekt;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Income;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;

public class InntektSupplier implements Supplier<LookupResult<Income>> {

   private final InntektClient inntektClient;
   private final Fodselsnummer fnr;
   private final int nrOfMonths;

   public InntektSupplier(
         InntektClient inntektClient,
         Fodselsnummer fnr,
         int nrOfMonths) {
      this.inntektClient = inntektClient;
      this.fnr = fnr;
      this.nrOfMonths = nrOfMonths;
   }

   @Override
   public LookupResult<Income> get() {
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(nrOfMonths);
      List<Income> incomeData = inntektClient.incomeForPeriod(fnr, earlier, now);
      return new LookupResult<>("Inntektskomponenten", LookupStatus.SUCCESS, incomeData);
   }
}
