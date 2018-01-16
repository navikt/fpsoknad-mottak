package no.nav.foreldrepenger.oppslag.infotrygd;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Benefit;

public class InfotrygdSupplier implements Supplier<LookupResult<Benefit>> {

   private final InfotrygdClient infotrygdClient;
   private final Fodselsnummer fnr;
   private final int nrOfMonths;

   public InfotrygdSupplier(
      InfotrygdClient infotrygdClient,
         Fodselsnummer fnr,
         int nrOfMonths) {
      this.infotrygdClient = infotrygdClient;
      this.fnr = fnr;
      this.nrOfMonths = nrOfMonths;
   }

   @Override
   public LookupResult<Benefit> get() {
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(nrOfMonths);
      List<Benefit> benefitData = infotrygdClient.casesFor(fnr.getFnr(), earlier, now);
      return new LookupResult<>("Arena", LookupStatus.SUCCESS, benefitData);
   }
}
