package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ETTERSENDING_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ETTERSENDING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ETTERSENDING_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.UKJENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface Inspektør {

    Logger LOG = LoggerFactory.getLogger(Inspektør.class);

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
        EttersendingsType type = ettersending.getType();
        switch (type) {
        case engangsstønad:
            return ETTERSENDING_ENGANGSSTØNAD;
        case foreldrepenger:
            return ETTERSENDING_FORELDREPENGER;
        case svangerskapspenger:
            return ETTERSENDING_SVANGERSKAPSPENGER;
        default:
            LOG.warn("Ukjent eller ikke satt ettersendingstype {}", type);
            throw new UnexpectedInputException("Ukjent eller ikke satt ettersendingstype %s", type);
        }
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
