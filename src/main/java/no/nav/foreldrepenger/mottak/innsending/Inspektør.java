package no.nav.foreldrepenger.mottak.innsending;

import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.ETTERSENDING_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.ETTERSENDING_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.ETTERSENDING_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.innsyn.SøknadEgenskap.UKJENT;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.Ytelse;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.Ettersending;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;

final class Inspektør {

    private Inspektør() {
    }

    static SøknadEgenskap inspiser(Søknad søknad) {
        Ytelse ytelse = søknad.getYtelse();
        if (ytelse instanceof Foreldrepenger) {
            return INITIELL_FORELDREPENGER;
        }
        if (ytelse instanceof Engangsstønad) {
            return INITIELL_ENGANGSSTØNAD;
        }
        if (ytelse instanceof Svangerskapspenger) {
            return INITIELL_SVANGERSKAPSPENGER;
        }
        return UKJENT;
    }

    static SøknadEgenskap inspiser(Ettersending ettersending) {
        return switch (ettersending.type()) {
            case FORELDREPENGER -> ETTERSENDING_FORELDREPENGER;
            case ENGANGSSTØNAD -> ETTERSENDING_ENGANGSSTØNAD;
            case SVANGERSKAPSPENGER -> ETTERSENDING_SVANGERSKAPSPENGER;
        };
    }
}
