package no.nav.foreldrepenger.mottak.innsyn.fpinfov2;

import java.util.Set;

public record Saker(Set<FpSak> foreldrepenger,
             Set<EsSak> engangsstønad,
             Set<SvpSak> svangerskapspenger) {
}
