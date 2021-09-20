package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;

public interface InnsendingHendelseProdusent {

    void publiser(Fødselsnummer fnr, Kvittering kvittering, String referanseId, Konvolutt konvolutt);
}
