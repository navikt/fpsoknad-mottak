package no.nav.foreldrepenger.mottak.innsyn;

import static no.nav.foreldrepenger.mottak.domain.felles.EttersendingsType.engangsstønad;
import static no.nav.foreldrepenger.mottak.domain.felles.EttersendingsType.foreldrepenger;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ETTERSENDING_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.ETTERSENDING_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_ENGANGSSTØNAD;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.INITIELL_FORELDREPENGER;
import static no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap.UKJENT;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_SVP_VERSJON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface SøknadInspektør {

    Logger LOG = LoggerFactory.getLogger(SøknadInspektør.class);

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
            return new SøknadEgenskap(DEFAULT_SVP_VERSJON, INITIELL_SVANGERSKAPSPENGER);
        }

        return UKJENT;
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

    default SøknadEgenskap inspiser(Ettersending ettersending) {
        if (foreldrepenger.equals(ettersending.getType())) {
            return ETTERSENDING_FORELDREPENGER;
        }
        if (engangsstønad.equals(ettersending.getType())) {
            return ETTERSENDING_ENGANGSSTØNAD;
        }
        LOG.warn("UKjent eller ikke satt ettersendingstype " + ettersending.getType());
        return ETTERSENDING_FORELDREPENGER;
        // throw new UnexpectedInputException("UKjent eller ikke satt ettersendingstype
        // " + ettersending.getType());

    }

}
