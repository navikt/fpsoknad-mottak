package no.nav.foreldrepenger.oppslag.lookup.ws.inntekt;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.oppslag.lookup.ws.aareg.TidsAvgrensetBrukerInfo;

public class Inntekt extends TidsAvgrensetBrukerInfo {

    private final double amount;
    private final String employer;

    public Inntekt(LocalDate from, Optional<LocalDate> to, double amount, String employer) {
        super(from, to);
        this.amount = amount;
        this.employer = employer;
    }

    public double getAmount() {
        return amount;
    }

    public String getEmployer() {
        return employer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Inntekt inntekt = (Inntekt) o;
        return Double.compare(inntekt.amount, amount) == 0 &&
                Objects.equals(employer, inntekt.employer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), amount, employer);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [amount=" + amount + ", employer=" + employer + "]";
    }

}
