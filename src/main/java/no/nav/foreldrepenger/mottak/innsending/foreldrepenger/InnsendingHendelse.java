package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.LeveranseStatus;
import no.nav.foreldrepenger.common.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.util.StringUtil;

public class InnsendingHendelse {

    private final AktørId aktørId;
    private final Fødselsnummer fnr;
    private final String journalId;
    private final String referanseId;
    private final String dialogId;
    private final String saksnummer;
    private final LeveranseStatus leveranseStatus;
    private final SøknadType hendelse;
    private final List<String> opplastedeVedlegg;
    private final List<String> ikkeOpplastedeVedlegg;
    private final LocalDateTime innsendt;

    private final LocalDate førsteBehandlingsdato;

    public InnsendingHendelse(AktørId aktørId, Fødselsnummer fnr, String dialogId, Kvittering kvittering,
            Konvolutt konvolutt) {
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
    }

    public Fødselsnummer getFnr() {
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

    public AktørId getAktørId() {
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[aktørId=" + aktørId + ",fnr=" + StringUtil.partialMask(fnr.getFnr())
                + ", journalId="
                + journalId
                + ", referanseId="
                + referanseId + ", dialogId=" + dialogId + ", saksnummer=" + saksnummer + ", leveranseStatus="
                + leveranseStatus + ", hendelse=" + hendelse + ", opplastedeVedlegg=" + opplastedeVedlegg
                + ", ikkeOpplastedeVedlegg=" + ikkeOpplastedeVedlegg + ", innsendt=" + innsendt
                + ", førsteBehandlingsdato=" + førsteBehandlingsdato + "]";
    }

}
