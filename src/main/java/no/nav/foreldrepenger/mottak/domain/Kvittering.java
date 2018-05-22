package no.nav.foreldrepenger.mottak.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.time.LocalDateTime.now;

import java.net.URI;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(NON_NULL)
public class Kvittering {

    public static final Kvittering IKKE_SENDT = new Kvittering("0", LocalDateTime.now(), null);
    private final String referanseId;
    private final LocalDateTime mottattDato;
    private final URI pollURI;

    public Kvittering(String referanseId) {
        this(referanseId, now());
    }

    public Kvittering(String referanseId, LocalDateTime motattDato) {
        this(referanseId, motattDato, null);
    }

    public Kvittering(String referanseId, URI pollURI) {
        this(referanseId, now(), pollURI);
    }

    @JsonCreator
    public Kvittering(@JsonProperty("referanseId") String referanseId,
            @JsonProperty("mottattDato") LocalDateTime mottattDato, URI pollURI) {
        this.referanseId = referanseId;
        this.mottattDato = mottattDato;
        this.pollURI = pollURI;
    }
}
