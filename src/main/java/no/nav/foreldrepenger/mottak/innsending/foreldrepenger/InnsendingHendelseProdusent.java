package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;

public interface InnsendingHendelseProdusent {

    void publiser(Fødselsnummer fnr, FordelResultat kvittering, String dialogId, Konvolutt konvolutt);
}
