package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.mottak.domain.FagsakType;
import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

public class InnsendingHendelse {

    private final String aktørId;
    private final String fnr;
    private final String journalId;
    private final String referanseId;
    private final String dialogId;
    private final String saksnummer;
    private final LeveranseStatus leveranseStatus;
    private final SøknadType hendelse;
    private final List<String> opplastedeVedlegg;
    private final List<String> ikkeOpplastedeVedlegg;
    private final LocalDateTime innsendt;
    private final FagsakType fagsakType;

    private final LocalDate førsteBehandlingsdato;

    public InnsendingHendelse(String aktørId, String fnr, String dialogId, Kvittering kvittering, Konvolutt konvolutt) {
        this.aktørId = aktørId;
        this.fnr = fnr;
        this.journalId = kvittering.getJournalId();
        this.referanseId = kvittering.getReferanseId();
        this.dialogId = dialogId;
        this.saksnummer = kvittering.getSaksNr();
        this.leveranseStatus = kvittering.getLeveranseStatus();
        this.hendelse = konvolutt.getType();
        this.opplastedeVedlegg = konvolutt.getOpplastedeVedlegg();
        this.ikkeOpplastedeVedlegg = konvolutt.getIkkeOpplastedeVedlegg();
        this.førsteBehandlingsdato = kvittering.getFørsteInntektsmeldingDag();
        this.innsendt = konvolutt.getOpprettet();
        this.fagsakType = konvolutt.getFagsakType();
    }

    public String getFnr() {
        return fnr;
    }

    public LocalDateTime getInnsendt() {
        return innsendt;
    }

    public String getDialogId() {
        return dialogId;
    }

    public LocalDate getFørsteBehandlingsdato() {
        return førsteBehandlingsdato;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getJournalId() {
        return journalId;
    }

    public String getReferanseId() {
        return referanseId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public LeveranseStatus getLeveranseStatus() {
        return leveranseStatus;
    }

    public SøknadType getHendelse() {
        return hendelse;
    }

    public List<String> getOpplastedeVedlegg() {
        return opplastedeVedlegg;
    }

    public List<String> getIkkeOpplastedeVedlegg() {
        return ikkeOpplastedeVedlegg;
    }

    public FagsakType getFagsakType() {
        return fagsakType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[aktørId=" + aktørId + ", fnr=" + fnr + ", journalId=" + journalId
                + ", referanseId=" + referanseId + ", dialogId=" + dialogId + ", saksnummer=" + saksnummer
                + ", leveranseStatus=" + leveranseStatus + ", hendelse=" + hendelse + ", opplastedeVedlegg="
                + opplastedeVedlegg + ", ikkeOpplastedeVedlegg=" + ikkeOpplastedeVedlegg + ", innsendt=" + innsendt
                + ", fagsakType=" + fagsakType + ", førsteBehandlingsdato=" + førsteBehandlingsdato + "]";
    }

}
