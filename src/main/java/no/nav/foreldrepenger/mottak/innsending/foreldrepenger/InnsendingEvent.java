package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.util.List;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

public class InnsendingEvent {

    private final String journalId;
    private final String referanseId;
    private final String saksNr;
    private final LeveranseStatus leveranseStatus;
    private final SøknadType type;
    private final String versjon;
    private List<String> vedlegg;

    public InnsendingEvent(Kvittering kvittering, SøknadEgenskap egenskap, List<String> vedlegg) {
        this.journalId = kvittering.getJournalId();
        this.referanseId = kvittering.getReferanseId();
        this.saksNr = kvittering.getSaksNr();
        this.leveranseStatus = kvittering.getLeveranseStatus();
        this.type = egenskap.getType();
        this.versjon = egenskap.getVersjon().name();
        this.vedlegg = vedlegg;
    }

    public String getJournalId() {
        return journalId;
    }

    public String getReferanseId() {
        return referanseId;
    }

    public String getSaksNr() {
        return saksNr;
    }

    public LeveranseStatus getLeveranseStatus() {
        return leveranseStatus;
    }

    public SøknadType getType() {
        return type;
    }

    public String getVersjon() {
        return versjon;
    }

    public List<String> getVedlegg() {
        return vedlegg;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [journalId=" + journalId + ", referanseId=" + referanseId + ", saksNr="
                + saksNr + ", leveranseStatus=" + leveranseStatus + ", type=" + type + ", versjon=" + versjon
                + ", vedlegg=" + vedlegg + "]";
    }
}
