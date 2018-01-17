package no.nav.foreldrepenger.oppslag.orchestrate;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.arena.ArenaSupplier;
import no.nav.foreldrepenger.oppslag.domain.Benefit;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Income;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Pair;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakKlient;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakSupplier;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdSupplier;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.inntekt.InntektSupplier;

@Component
public class CoordinatedLookup {

   private InntektClient inntektClient;
   private ArenaClient arenaClient;
   private FpsakKlient fpsakClient;
   private InfotrygdClient infotrygdClient;

   @Inject
   public CoordinatedLookup(
         InntektClient inntektClient,
         ArenaClient arenaClient,
         FpsakKlient fpsakClient,
         InfotrygdClient infotrygdClient) {
      this.inntektClient = inntektClient;
      this.arenaClient = arenaClient;
      this.fpsakClient = fpsakClient;
      this.infotrygdClient = infotrygdClient;
   }

   public Pair<List<LookupResult<Income>>, List<LookupResult<Benefit>>> gimmeAllYouGot(ID person) {

      CompletableFuture<LookupResult<Income>> inntektskomponenten =
         CompletableFuture.supplyAsync(new InntektSupplier(inntektClient, person.getFnr(), 12))
         .handle((l, t) -> l != null ? l :
            new LookupResult<>("Inntektskomponenten", LookupStatus.FAILURE, Collections.emptyList(), t.getMessage()));

      CompletableFuture<LookupResult<Benefit>> arena =
         CompletableFuture.supplyAsync(new ArenaSupplier(arenaClient, person.getFnr(), 60))
            .handle((l, t) -> l != null ? l :
               new LookupResult<>("Arena", LookupStatus.FAILURE, Collections.emptyList(), t.getMessage()));

      CompletableFuture<LookupResult<Benefit>> fpsak =
         CompletableFuture.supplyAsync(new FpsakSupplier(fpsakClient, person.getAktorId()))
            .handle((l, t) -> l != null ? l :
               new LookupResult<>("Fpsak", LookupStatus.FAILURE, Collections.emptyList(), t.getMessage()));

      CompletableFuture<LookupResult<Benefit>> infotrygd =
         CompletableFuture.supplyAsync(new InfotrygdSupplier(infotrygdClient, person.getFnr(), 60))
            .handle((l, t) -> l != null ? l :
               new LookupResult<>("Infotrygd", LookupStatus.FAILURE, Collections.emptyList(), t.getMessage()));

      List<LookupResult<Income>> income = Stream.of(inntektskomponenten)
         .map(CompletableFuture::join)
         .collect(toList());

      List<LookupResult<Benefit>> benefits = Stream.of(arena, fpsak, infotrygd)
         .map(CompletableFuture::join)
         .collect(toList());

      return Pair.of(income, benefits);
   }

}
