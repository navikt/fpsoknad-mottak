package no.nav.foreldrepenger.oppslag.orchestrate;

import no.nav.foreldrepenger.oppslag.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.aareg.AaregSupplier;
import no.nav.foreldrepenger.oppslag.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.arena.ArenaSupplier;
import no.nav.foreldrepenger.oppslag.domain.*;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakClient;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakSupplier;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdSupplier;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.inntekt.InntektSupplier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@Component
public class CoordinatedLookup {

    private final InntektClient inntekt;
    private final ArenaClient arena;
    private final FpsakClient fpsak;
    private final InfotrygdClient infotrygd;
    private final AaregClient aareg;

    @Inject
    public CoordinatedLookup(
         InntektClient inntekt,
         ArenaClient arena,
         FpsakClient fpsak,
         InfotrygdClient infotrygd,
         AaregClient aareg) {
       this.inntekt = inntekt;
       this.arena = arena;
       this.fpsak = fpsak;
       this.infotrygd = infotrygd;
       this.aareg = aareg;
    }

    public AggregatedLookupResults gimmeAllYouGot(ID person) {

        CompletableFuture<LookupResult<Inntekt>> inntektskomponenten = CompletableFuture
                .supplyAsync(new InntektSupplier(inntekt, person.getFnr(), 12))
                .handle((l, t) -> l != null ? l : error("Inntektskomponenten", t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> arenaYtelser = CompletableFuture
                .supplyAsync(new ArenaSupplier(arena, person.getFnr(), 60))
                .handle((l, t) -> l != null ? l : error("Arena", t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> infotrygdYtelser = CompletableFuture
                .supplyAsync(new InfotrygdSupplier(infotrygd, person.getFnr(), 60))
                .handle((l, t) -> l != null ? l : error("Infotrygd", t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> fpsakYtelser = CompletableFuture
                .supplyAsync(new FpsakSupplier(fpsak, person.getAktorId()))
                .handle((l, t) -> l != null ? l : error("FPSAK", t.getMessage()));

       CompletableFuture<LookupResult<Arbeidsforhold>> aaregArbeid = CompletableFuture
          .supplyAsync(new AaregSupplier(aareg, person.getFnr(), 60))
          .handle((l, t) -> l != null ? l : error("AAREG", t.getMessage()));

        return new AggregatedLookupResults(
           resultaterFra(inntektskomponenten),
           resultaterFra(arenaYtelser, infotrygdYtelser, fpsakYtelser),
           resultaterFra(aaregArbeid)
        );
    }

    @SafeVarargs
    private final <T extends TidsAvgrensetBrukerInfo> List<LookupResult<T>> resultaterFra(
            CompletableFuture<LookupResult<T>>... systemer) {
        return Arrays.stream(systemer)
           .map(CompletableFuture::join)
           .collect(toList());
    }

    private static <T extends TidsAvgrensetBrukerInfo> LookupResult<T> error(String system, String errMsg) {
        return new LookupResult<>(system, LookupStatus.FAILURE, Collections.emptyList(), errMsg);
    }

   @Override
   public String toString() {
      return "CoordinatedLookup{" +
         "inntekt=" + inntekt +
         ", arena=" + arena +
         ", fpsak=" + fpsak +
         ", infotrygd=" + infotrygd +
         ", aareg=" + aareg +
         '}';
   }
}
