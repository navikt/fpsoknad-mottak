package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsending.SøknadType;

record InnsendingHendelse(AktørId aktørId,
                          Fødselsnummer fnr,
                          String journalId,
                          String referanseId,
                          String dialogId,
                          String saksnummer,
                          SøknadType hendelse,
                          List<String> opplastedeVedlegg,
                          List<String> ikkeOpplastedeVedlegg,
                          LocalDateTime innsendt,
                          LocalDate førsteBehandlingsdato) {

    public InnsendingHendelse(AktørId aktørId,
                              Fødselsnummer fnr,
                              String journalId,
                              String referanseId,
                              String dialogId,
                              String saksnummer,
                              Konvolutt konvolutt,
                              LocalDate førsteBehandlingsdato) {
        this(aktørId, fnr, journalId, referanseId, dialogId, saksnummer,
            konvolutt.getType(), konvolutt.getOpplastedeVedlegg(), konvolutt.getIkkeOpplastedeVedlegg(),
            konvolutt.getOpprettet(), førsteBehandlingsdato);
    }


}
