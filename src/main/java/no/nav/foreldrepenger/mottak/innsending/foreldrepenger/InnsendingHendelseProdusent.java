package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

public interface InnsendingHendelseProdusent {

    void publiser(FordelResultat kvittering, String dialogId, Konvolutt konvolutt, InnsendingPersonInfo person);
}
