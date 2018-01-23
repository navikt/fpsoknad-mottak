package no.nav.foreldrepenger.oppslag.orchestrate;

import no.nav.foreldrepenger.oppslag.Register;
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
import static no.nav.foreldrepenger.oppslag.Register.INNTEKTSMELDING;

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
                .handle((l, t) -> l != null ? l : error(INNTEKTSMELDING, t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> arenaYtelser = CompletableFuture
                .supplyAsync(new ArenaSupplier(arena, person.getFnr(), 60))
                .handle((l, t) -> l != null ? l : error(Register.ARENA, t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> infotrygdYtelser = CompletableFuture
                .supplyAsync(new InfotrygdSupplier(infotrygd, person.getFnr(), 60))
                .handle((l, t) -> l != null ? l : error(Register.INFOTRYGD, t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> fpsakYtelser = CompletableFuture
                .supplyAsync(new FpsakSupplier(fpsak, person.getAktorId()))
                .handle((l, t) -> l != null ? l : error(Register.FPSAK, t.getMessage()));

       CompletableFuture<LookupResult<Arbeidsforhold>> aaregArbeid = CompletableFuture
          .supplyAsync(new AaregSupplier(aareg, person.getFnr(), 60))
          .handle((l, t) -> l != null ? l : error(Register.AAREG, t.getMessage()));

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

    private static <T extends TidsAvgrensetBrukerInfo> LookupResult<T> error(Register system, String errMsg) {
        return new LookupResult<T>(system.getDisplayValue(), LookupStatus.FAILURE, Collections.emptyList(), errMsg);
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
