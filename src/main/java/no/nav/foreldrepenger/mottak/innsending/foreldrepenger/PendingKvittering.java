package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.time.Duration;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PendingKvittering extends FordelKvittering {

    static final String STATUS = "PENDING";
    @JsonFormat(shape = STRING)
    private final Duration pollInterval;

    public Duration getPollInterval() {
        return pollInterval;
    }

    @JsonCreator
    public PendingKvittering(@JsonProperty("pollInterval") Duration pollInterval) {
        super(STATUS);
        this.pollInterval = pollInterval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pollInterval);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PendingKvittering other = (PendingKvittering) obj;
        return Objects.equals(pollInterval, other.pollInterval);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pollInterval=" + pollInterval + "]";
    }
}
