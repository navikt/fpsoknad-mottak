package no.nav.foreldrepenger.mottak.dokmot;

import static no.nav.foreldrepenger.mottak.domain.VedleggSkjemanummer.TERMINBEKREFTELSE;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.Søknad;

final class JukseVedlegg {

    private static final String TERMINBEKREFTELSE_PDF = "terminbekreftelse.pdf";
    private static final Logger LOG = getLogger(JukseVedlegg.class);

    private JukseVedlegg() {

    }

    static List<PåkrevdVedlegg> påkrevdVedlegg(Søknad søknad) {
        return påkrevdVedleggMedEventueltJuks(søknad);
    }

    private static List<PåkrevdVedlegg> påkrevdVedleggMedEventueltJuks(Søknad søknad) {
        return skalJukse(søknad) ? juks(søknad) : søknad.getPåkrevdeVedlegg();
    }

    private static boolean skalJukse(Søknad søknad) {
        return (Engangsstønad.class.cast(søknad.getYtelse()).getRelasjonTilBarn() instanceof FremtidigFødsel)
                && søknad.getPåkrevdeVedlegg().isEmpty();
    }

    private static List<PåkrevdVedlegg> juks(Søknad søknad) {
        try {
            LOG.info("Ingen påkrevde vedlegg funnet for fremtidig fødsel, prøver å jukse med '{}'",
                    TERMINBEKREFTELSE_PDF);
            PåkrevdVedlegg påkrevdVedlegg = new PåkrevdVedlegg(TERMINBEKREFTELSE,
                    new ClassPathResource(TERMINBEKREFTELSE_PDF));
            LOG.info("Og det gikk fint {}", påkrevdVedlegg);
            return Collections.singletonList(påkrevdVedlegg);
        } catch (IOException e) {
            LOG.info("Fant ikke dummy terminbekreftelse, gjorde så god jeg kunne");
            return søknad.getPåkrevdeVedlegg();
        }
    }
}
