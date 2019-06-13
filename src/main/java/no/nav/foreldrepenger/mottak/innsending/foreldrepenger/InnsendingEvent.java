package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class InnsendingEvent {

    private final String journalId;
    private final String referanseId;
    private final String saksNr;
    private final LeveranseStatus leveranseStatus;
    private final SøknadType type;
    private final Versjon versjon;
    private List<String> vedlegg;

    public InnsendingEvent(Kvittering kvittering, SøknadEgenskap egenskap, List<String> vedlegg) {
        this.journalId = kvittering.getJournalId();
        this.referanseId = kvittering.getReferanseId();
        this.saksNr = kvittering.getSaksNr();
        this.leveranseStatus = kvittering.getLeveranseStatus();
        this.type = egenskap.getType();
        this.versjon = egenskap.getVersjon();
        this.vedlegg = vedlegg;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [journalId=" + journalId + ", referanseId=" + referanseId + ", saksNr="
                + saksNr + ", leveranseStatus=" + leveranseStatus + ", type=" + type + ", versjon=" + versjon
                + ", vedlegg=" + vedlegg + "]";
    }
}
