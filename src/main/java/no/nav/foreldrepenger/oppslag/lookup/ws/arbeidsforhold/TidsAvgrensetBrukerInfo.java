package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public abstract class TidsAvgrensetBrukerInfo {

    private final LocalDate from;
    private final Optional<LocalDate> to;

    public TidsAvgrensetBrukerInfo(LocalDate from, Optional<LocalDate> to) {
        this.from = from;
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public Optional<LocalDate> getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        TidsAvgrensetBrukerInfo that = (TidsAvgrensetBrukerInfo) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "TidsAvgrensetBrukerInfo{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
