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
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Pair;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakClient;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakSupplier;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdSupplier;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.inntekt.InntektSupplier;

@Component
public class CoordinatedLookup {

	private final InntektClient inntektClient;
	private final ArenaClient arenaClient;
	private final FpsakClient fpsakClient;
	private final InfotrygdClient infotrygdClient;

	@Inject
	public CoordinatedLookup(InntektClient inntektClient, ArenaClient arenaClient, FpsakClient fpsakClient,
	        InfotrygdClient infotrygdClient) {
		this.inntektClient = inntektClient;
		this.arenaClient = arenaClient;
		this.fpsakClient = fpsakClient;
		this.infotrygdClient = infotrygdClient;
	}

	public Pair<List<LookupResult<Inntekt>>, List<LookupResult<Ytelse>>> gimmeAllYouGot(ID person) {

		CompletableFuture<LookupResult<Inntekt>> inntektskomponenten = CompletableFuture
		        .supplyAsync(new InntektSupplier(inntektClient, person.getFnr(), 12))
		        .handle((l, t) -> l != null ? l
		                : new LookupResult<>("Inntektskomponenten", LookupStatus.FAILURE,
		                        Collections.<Inntekt>emptyList(), t.getMessage()));

		CompletableFuture<LookupResult<Ytelse>> arena = CompletableFuture
		        .supplyAsync(new ArenaSupplier(arenaClient, person.getFnr(), 60))
		        .handle((l, t) -> l != null ? l
		                : new LookupResult<>("Arena", LookupStatus.FAILURE, Collections.<Ytelse>emptyList(),
		                        t.getMessage()));

		CompletableFuture<LookupResult<Ytelse>> fpsak = CompletableFuture
		        .supplyAsync(new FpsakSupplier(fpsakClient, person.getAktorId()))
		        .handle((l, t) -> l != null ? l
		                : new LookupResult<>("Fpsak", LookupStatus.FAILURE, Collections.<Ytelse>emptyList(),
		                        t.getMessage()));

		CompletableFuture<LookupResult<Ytelse>> infotrygd = CompletableFuture
		        .supplyAsync(new InfotrygdSupplier(infotrygdClient, person.getFnr(), 60))
		        .handle((l, t) -> l != null ? l
		                : new LookupResult<>("Infotrygd", LookupStatus.FAILURE, Collections.<Ytelse>emptyList(),
		                        t.getMessage()));

		return Pair.of(inntektFra(inntektskomponenten), ytelserFra(arena, fpsak, infotrygd));
	}

	private List<LookupResult<Inntekt>> inntektFra(CompletableFuture<LookupResult<Inntekt>> inntektskomponenten) {
		return Stream.of(inntektskomponenten).map(CompletableFuture::join)
		        .collect(toList());
	}

	private List<LookupResult<Ytelse>> ytelserFra(CompletableFuture<LookupResult<Ytelse>> arena,
	        CompletableFuture<LookupResult<Ytelse>> fpsak, CompletableFuture<LookupResult<Ytelse>> infotrygd) {
		return Stream.of(arena, fpsak, infotrygd).map(CompletableFuture::join)
		        .collect(toList());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [inntektClient=" + inntektClient + ", arenaClient=" + arenaClient + ", fpsakClient="
		        + fpsakClient + ", infotrygdClient=" + infotrygdClient + "]";
	}

}
