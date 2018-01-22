package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.oppslag.Register;

public abstract class TidsAvgrensetBrukerInfo {

    private final String register;
    private final String status;
    private final LocalDate from;
    private final Optional<LocalDate> to;

    public TidsAvgrensetBrukerInfo(Register register, String status, LocalDate from, Optional<LocalDate> to) {
        this(register.getDisplayValue(), status, from, to);
    }

    public TidsAvgrensetBrukerInfo(String register, String status, LocalDate from, Optional<LocalDate> to) {
        this.register = register;
        this.status = status;
        this.from = from;
        this.to = to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(register, status, from, to);
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
        TidsAvgrensetBrukerInfo other = (TidsAvgrensetBrukerInfo) obj;
        return Objects.equals(register, other.register) && Objects.equals(status, other.status)
                && Objects.equals(from, other.from) && Objects.equals(to, other.to);
    }

    public String getRegister() {
        return register;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getFrom() {
        return from;
    }

    public Optional<LocalDate> getTo() {
        return to;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [register=" + register + ", status=" + status + ", from=" + from + ", to="
                + to
                + "]";
    }

}
