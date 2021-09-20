package no.nav.foreldrepenger.mottak.innsyn.uttaksplan;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.util.List;

public record Uttaksplan(SøknadsGrunnlag grunnlag, List<UttaksPeriode> perioder) {
    public List<UttaksPeriode> getPerioder() {
        return safeStream(perioder).sorted().toList();
    }
}
