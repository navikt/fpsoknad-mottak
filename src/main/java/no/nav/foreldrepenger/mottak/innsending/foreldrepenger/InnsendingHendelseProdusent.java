package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

public interface InnsendingHendelseProdusent {

    void publiser(Kvittering kvittering, String referanseId, Konvolutt konvolutt);
}
