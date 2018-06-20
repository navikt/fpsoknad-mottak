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

    public Kvittering(String ref, LeveranseStatus leveranseStatus) {
        this(ref, LocalDateTime.now(), leveranseStatus);
    }

    @JsonCreator
    public Kvittering(@JsonProperty("referanseId") String referanseId,
            @JsonProperty("mottattDato") LocalDateTime mottattDato,
            @JsonProperty("leveranseStatus") LeveranseStatus leveranseStatus) {
        this.referanseId = referanseId;
        this.mottattDato = mottattDato;
        this.leveranseStatus = leveranseStatus;
    }
}
