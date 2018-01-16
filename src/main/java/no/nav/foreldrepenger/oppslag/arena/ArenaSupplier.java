package no.nav.foreldrepenger.oppslag.arena;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Benefit;

public class ArenaSupplier implements Supplier<LookupResult<Benefit>> {

   private final ArenaClient arenaClient;
   private final Fodselsnummer fnr;
   private final int nrOfMonths;

   public ArenaSupplier(
         ArenaClient arenaClient,
         Fodselsnummer fnr,
         int nrOfMonths) {
      this.arenaClient = arenaClient;
      this.fnr = fnr;
      this.nrOfMonths = nrOfMonths;
   }

   @Override
   public LookupResult<Benefit> get() {
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(nrOfMonths);
      List<Benefit> benefitData = arenaClient.ytelser(fnr.getFnr(), earlier, now);
      return new LookupResult<>("Arena", LookupStatus.SUCCESS, benefitData);
   }
}
