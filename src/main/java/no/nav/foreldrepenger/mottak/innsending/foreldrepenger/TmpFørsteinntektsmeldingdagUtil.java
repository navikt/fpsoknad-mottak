package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.boot.conditionals.Cluster;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.*;

import java.time.LocalDate;
import java.util.Optional;

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
            : nyFørsteUttaksdag(søknad);
    }

    private static LocalDate nyFørsteUttaksdag(Søknad søknad) {
        var fp = (Foreldrepenger) søknad.getYtelse();
        return fp.fordeling().perioder().stream()
            .sorted()
            .filter(p -> !(p instanceof FriUtsettelsesPeriode))
            .filter(p -> p instanceof UttaksPeriode || p instanceof UtsettelsesPeriode || p instanceof OverføringsPeriode)
            .findFirst()
            .map(LukketPeriodeMedVedlegg::getFom)
            .orElse(null);
    }

    private static LocalDate nyInntektsmeldingDag(Søknad søknad) {
        return Optional.ofNullable(nyFørsteUttaksdag(søknad))
            .map(d -> d.minusWeeks(4))
            .orElse(null);
    }

}
