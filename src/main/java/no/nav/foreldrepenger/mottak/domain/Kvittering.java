package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.IKKE_SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static no.nav.foreldrepenger.mottak.util.StringUtil.limit;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(NON_NULL)
public class Kvittering {

    private final String referanseId;
    private final LocalDateTime mottattDato;
    private final LeveranseStatus leveranseStatus;
    private String journalId;
    private String saksNr;
    private byte[] pdf;

    public Kvittering(LeveranseStatus leveranseStatus) {
        this(leveranseStatus, LocalDateTime.now(), callId());
    }

    @JsonCreator
    public Kvittering(@JsonProperty("leveranseStatus") LeveranseStatus leveranseStatus,
            @JsonProperty("mottattDato") LocalDateTime mottattDato,
            @JsonProperty("referanseId") String referanseId) {
        this.referanseId = referanseId;
        this.mottattDato = mottattDato;
        this.leveranseStatus = leveranseStatus;
    }

    public boolean erVellykket() {
        return leveranseStatus.erVellykket();
    }

    public boolean erUklarStatus() {
        return FP_FORDEL_MESSED_UP.equals(leveranseStatus);
    }

    public static Kvittering ikkeSendt(byte[] pdf) {
        Kvittering kvittering = new Kvittering(IKKE_SENDT_FPSAK);
        kvittering.setPdf(pdf);
        return kvittering;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [referanseId=" + referanseId + ", mottattDato=" + mottattDato
                + ", leveranseStatus=" + leveranseStatus + ", journalId=" + journalId + ", saksNr=" + saksNr + ", pdf="
                + limit(pdf, 50) + "]";
    }
}
