package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Inntekt extends TidsAvgrensetBrukerInfo {

    private double amount;

    public Inntekt(double amount, LocalDate from, Optional<LocalDate> to) {
        super(from, to);
        this.amount = amount;
    }

    public double amount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Inntekt{" +
                "amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Inntekt inntekt = (Inntekt) o;
        return Double.compare(inntekt.amount, amount) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), amount);
    }
}
