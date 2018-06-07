package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(NON_NULL)
public class Kvittering {

    public static final Kvittering IKKE_SENDT = new Kvittering("0", LocalDateTime.now());
    private final String referanseId;
    private final LocalDateTime mottattDato;

    public Kvittering(String referanseId) {
        this(referanseId, now());
    }

    @JsonCreator
    public Kvittering(@JsonProperty("referanseId") String referanseId,
            @JsonProperty("mottattDato") LocalDateTime mottattDato) {
        this.referanseId = referanseId;
        this.mottattDato = mottattDato;
    }
}
