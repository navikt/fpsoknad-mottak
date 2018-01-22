package no.nav.foreldrepenger.oppslag.orchestrate;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.oppslag.Register.INNTEKTSMELDING;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.Register;
import no.nav.foreldrepenger.oppslag.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.arena.ArenaSupplier;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Pair;
import no.nav.foreldrepenger.oppslag.domain.TidsAvgrensetBrukerInfo;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakClient;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakSupplier;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdSupplier;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.inntekt.InntektSupplier;

@Component
public class CoordinatedLookup {

    private final InntektClient inntekt;
    private final ArenaClient arena;
    private final FpsakClient fpsak;
    private final InfotrygdClient infotrygd;

    @Inject
    public CoordinatedLookup(InntektClient inntekt, ArenaClient arena, FpsakClient fpsak, InfotrygdClient infotrygd) {
        this.inntekt = inntekt;
        this.arena = arena;
        this.fpsak = fpsak;
        this.infotrygd = infotrygd;
    }

    public Pair<List<LookupResult<Inntekt>>, List<LookupResult<Ytelse>>> gimmeAllYouGot(
            ID person) {

        CompletableFuture<LookupResult<Inntekt>> inntektskomponenten = CompletableFuture
                .supplyAsync(new InntektSupplier(inntekt, person.getFnr(), 12))
                .handle((l, t) -> l != null ? l : error(INNTEKTSMELDING, t.getMessage()));
        CompletableFuture<LookupResult<Ytelse>> arenaYtelser = CompletableFuture
                .supplyAsync(new ArenaSupplier(arena, person.getFnr(), 60))
                .handle((l, t) -> l != null ? l
                        : error(Register.ARENA,
                                t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> infotrygdYtelser = CompletableFuture
                .supplyAsync(new InfotrygdSupplier(infotrygd, person.getFnr(), 60))
                .handle((l, t) -> l != null ? l : error(Register.FPSAK, t.getMessage()));

        CompletableFuture<LookupResult<Ytelse>> fpsakYtelser = CompletableFuture
                .supplyAsync(new FpsakSupplier(fpsak, person.getAktorId()))
                .handle((l, t) -> l != null ? l : error(Register.FPSAK, t.getMessage()));

        return Pair.of(resultaterFra(inntektskomponenten),
                resultaterFra(/* arenaYtelser, */fpsakYtelser, infotrygdYtelser));
    }

    @SafeVarargs
    private final <T extends TidsAvgrensetBrukerInfo> List<LookupResult<T>> resultaterFra(
            CompletableFuture<LookupResult<T>>... systemer) {
        return Arrays.stream(systemer).map(CompletableFuture::join).collect(toList());
    }

    private static <T extends TidsAvgrensetBrukerInfo> LookupResult<T> error(Register system, String errMsg) {
        return new LookupResult<T>(system.getDisplayValue(), LookupStatus.FAILURE, Collections.emptyList(), errMsg);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [inntekt=" + inntekt + ", arena=" + arena + ", fpsak=" + fpsak
                + ", infotrygd=" + infotrygd + "]";
    }

}
