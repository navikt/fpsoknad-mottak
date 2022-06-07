package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

import no.nav.boot.conditionals.Cluster;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.*;

// flytt logikk til fpsoknad-felles når test er ferdig
public final class TmpFørsteinntektsmeldingdagUtil {

    private TmpFørsteinntektsmeldingdagUtil() {
    }

    public static LocalDate førsteInntektsmeldingDag(Søknad søknad) {
        var prod = Cluster.PROD_FSS == Cluster.currentCluster();
        return prod
            ? søknad.getFørsteInntektsmeldingDag()
            : nyInntektsmeldingDag(søknad);
    }

    public static LocalDate førsteUttaksdag(Søknad søknad) {
        var prod = Cluster.PROD_FSS == Cluster.currentCluster();
        return prod
            ? søknad.getFørsteUttaksdag()
            : kandidatFunksjonForFørsteUttaksdag(søknad);
    }

    private static LocalDate kandidatFunksjonForFørsteUttaksdag(Søknad søknad) {
        var fp = (Foreldrepenger) søknad.getYtelse();
        return fp.fordeling()
            .perioder().stream()
            .sorted()
            .filter(not(erFriPeriode()))
            .filter(p -> p instanceof UttaksPeriode || p instanceof UtsettelsesPeriode || p instanceof OverføringsPeriode)
            .findFirst()
            .map(LukketPeriodeMedVedlegg::getFom)
            .orElse(null);
    }

    private static LocalDate nyInntektsmeldingDag(Søknad søknad) {
        return Optional.ofNullable(kandidatFunksjonForFørsteUttaksdag(søknad))
            .map(d -> d.minusWeeks(4))
            .orElse(null);
    }

    private static Predicate<? super LukketPeriodeMedVedlegg> erFriPeriode() {
        return p -> p instanceof UtsettelsesPeriode && ((UtsettelsesPeriode) p).getÅrsak().equals(UtsettelsesÅrsak.FRI);
    }

}
