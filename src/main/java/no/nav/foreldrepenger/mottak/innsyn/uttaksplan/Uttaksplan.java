package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.util.List;

public record Uttaksplan(SøknadsGrunnlag grunnlag, List<UttaksPeriode> perioder) {
    public List<UttaksPeriode> getPerioder() {
        return safeStream(perioder).sorted().collect(toList());
    }
}
