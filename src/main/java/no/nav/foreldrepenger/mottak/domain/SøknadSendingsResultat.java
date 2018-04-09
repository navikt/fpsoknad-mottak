package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SøknadSendingsResultat {

    private final String ref;
    private final LocalDateTime mottattDato;
    private final String message;

    public SøknadSendingsResultat(String message) {
        this(null, LocalDateTime.now(), message);
    }

    public SøknadSendingsResultat(String reference, String message) {
        this(reference, LocalDateTime.now(), message);
    }

    @JsonCreator
    public SøknadSendingsResultat(@JsonProperty("reference") String reference,
            @JsonProperty("mottattDato") LocalDateTime mottattDato,
            @JsonProperty("message") String message) {
        this.ref = reference;
        this.mottattDato = mottattDato;
        this.message = message;
    }

    public String getRef() {
        return ref;
    }

    public LocalDateTime getMottattDato() {
        return mottattDato;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mottattDato, ref, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SøknadSendingsResultat other = (SøknadSendingsResultat) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        }
        else if (!message.equals(other.message))
            return false;
        if (mottattDato == null) {
            if (other.mottattDato != null)
                return false;
        }
        else if (!mottattDato.equals(other.mottattDato))
            return false;
        if (ref == null) {
            if (other.ref != null)
                return false;
        }
        else if (!ref.equals(other.ref))
            return false;
        return true;
    }

}
