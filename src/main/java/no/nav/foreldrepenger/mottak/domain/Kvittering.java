package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Kvittering {

    public static final Kvittering IKKE_SENDT = new Kvittering("0", LocalDateTime.now());
    private final String referanseId;
    private final LocalDateTime mottattDato;

    public Kvittering(String referanseId) {
        this(referanseId, LocalDateTime.now());
    }

    @JsonCreator
    public Kvittering(@JsonProperty("referanseId") String referanseId,
            @JsonProperty("mottattDato") LocalDateTime mottattDato) {
        this.referanseId = referanseId;
        this.mottattDato = mottattDato;
    }
}
