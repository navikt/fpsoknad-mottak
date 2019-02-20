package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;

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

    public static final Kvittering IKKE_SENDT = new Kvittering(LeveranseStatus.IKKE_SENDT_FPSAK);

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
}
