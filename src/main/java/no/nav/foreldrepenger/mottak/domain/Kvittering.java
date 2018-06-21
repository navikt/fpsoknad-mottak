package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

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

    public Kvittering(LeveranseStatus leveranseStatus, String ref) {
        this(leveranseStatus, LocalDateTime.now(), ref);
    }

    @JsonCreator
    public Kvittering(@JsonProperty("leveranseStatus") LeveranseStatus leveranseStatus,
            @JsonProperty("mottattDato") LocalDateTime mottattDato,
            @JsonProperty("referanseId") String referanseId) {
        this.referanseId = referanseId;
        this.mottattDato = mottattDato;
        this.leveranseStatus = leveranseStatus;
    }
}
