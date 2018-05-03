package no.nav.foreldrepenger.oppslag.http.lookup;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import no.nav.foreldrepenger.oppslag.http.lookup.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.http.lookup.aareg.AaregSupplier;
import no.nav.foreldrepenger.oppslag.http.lookup.aareg.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.http.lookup.aareg.TidsAvgrensetBrukerInfo;
import no.nav.foreldrepenger.oppslag.http.lookup.inntekt.Inntekt;
import no.nav.foreldrepenger.oppslag.http.lookup.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.http.lookup.inntekt.InntektSupplier;
import no.nav.foreldrepenger.oppslag.http.lookup.medl.MedlClient;
import no.nav.foreldrepenger.oppslag.http.lookup.medl.MedlPeriode;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.arena.ArenaSupplier;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.fpsak.FpsakSupplier;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd.InfotrygdClient;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.infotrygd.InfotrygdSupplier;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.http.lookup.person.ID;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.fpsak.FpsakClient;
import no.nav.foreldrepenger.oppslag.http.lookup.medl.MedlSupplier;

@Component
public class CoordinatedLookup {

    private final InntektClient inntekt;
    private final ArenaClient arena;
    private final FpsakClient fpsak;
    private final InfotrygdClient infotrygd;
    private final AaregClient aareg;
    private final MedlClient medl;

    @Inject
    public CoordinatedLookup(
            InntektClient inntekt,
            ArenaClient arena,
            FpsakClient fpsak,
            InfotrygdClient infotrygd,
            AaregClient aareg,
            MedlClient medl) {
        this.inntekt = inntekt;
        this.arena = arena;
        this.fpsak = fpsak;
        this.infotrygd = infotrygd;
        this.aareg = aareg;
        this.medl = medl;
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
                .supplyAsync(new AaregSupplier(aareg, person.getFnr()))
                .handle((l, t) -> l != null ? l : error("AAREG", t.getMessage()));

        CompletableFuture<LookupResult<MedlPeriode>> medlPerioder = CompletableFuture
                .supplyAsync(new MedlSupplier(medl, person.getFnr()))
                .handle((l, t) -> l != null ? l : error("MEDL", t.getMessage()));

        return new AggregatedLookupResults(
                resultaterFra(inntektskomponenten),
                resultaterFra(arenaYtelser, infotrygdYtelser, fpsakYtelser),
                resultaterFra(aaregArbeid),
                resultaterFra(medlPerioder));
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
                ", medl=" + medl +
                '}';
    }
}
