package no.nav.foreldrepenger.mottak.dokmot;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.Skjemanummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;

class JukseVedlegg {

    private static final Logger LOG = getLogger(JukseVedlegg.class);

    private JukseVedlegg() {

    }

    static List<PåkrevdVedlegg> påkrevdVedlegg(Søknad søknad) {
        Engangsstønad engangsstønad = Engangsstønad.class.cast(søknad.getYtelse());
        try {
            if (engangsstønad.getRelasjonTilBarn() instanceof FremtidigFødsel
                    && søknad.getPåkrevdeVedlegg().isEmpty()) {
                LOG.info(
                        "Ingen påkrevde vedlegg funnet for fremtidig fødsel, prøver å bruke dummy terminbekreftelse 'terminbekreftelse.pdf'");
                PåkrevdVedlegg påkrevdVedlegg = påkrevdVedlegg("terminbekreftelse.pdf");
                LOG.info("Og det gikk fint {}", påkrevdVedlegg);
                return Collections.singletonList(påkrevdVedlegg);
            }
            LOG.info("Påkrevd vedlegg allerede sendt med");
            return søknad.getPåkrevdeVedlegg();
        } catch (IOException e) {
            LOG.info("Kunne ikke bruke dummy terminbekreftelse");
            return søknad.getPåkrevdeVedlegg();
        }
    }

    private static PåkrevdVedlegg påkrevdVedlegg(String name) throws IOException {
        return new PåkrevdVedlegg(Skjemanummer.N6, new ClassPathResource(name));
    }

}
