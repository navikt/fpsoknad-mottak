package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

public interface InnsendingHendelseProdusent {

    void publiser(Kvittering kvittering, SøknadType type, List<String> vedlegg);
}
