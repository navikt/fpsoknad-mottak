package no.nav.foreldrepenger.mottak.innsyn;

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
import no.nav.foreldrepenger.common.error.UnexpectedInputException;
import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.util.Versjon;

public interface Inspektør {
    SøknadEgenskap inspiser(String xml);

    default SøknadEgenskap inspiser(Søknad søknad) {
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

    default SøknadEgenskap inspiser(Ettersending ettersending) {
        return switch (ettersending.getType()) {
            case engangsstønad -> ETTERSENDING_ENGANGSSTØNAD;
            case foreldrepenger -> ETTERSENDING_FORELDREPENGER;
            case svangerskapspenger -> ETTERSENDING_SVANGERSKAPSPENGER;
            default -> throw new UnexpectedInputException("Ukjent eller ikke satt ettersendingstype %s", ettersending.getType());
        };
    }

    default SøknadType type(Søknad søknad) {
        return inspiser(søknad).getType();
    }

    default SøknadType type(String xml) {
        return inspiser(xml).getType();
    }

    default Versjon versjon(String xml) {
        return inspiser(xml).getVersjon();
    }
}
