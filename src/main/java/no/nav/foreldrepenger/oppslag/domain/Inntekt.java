package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Inntekt extends TidsAvgrensetBrukerInfo {

    private double amount;
    private String employer;

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
   public String toString() {
      return "Inntekt{" +
         "amount=" + amount +
         ", employer='" + employer + '\'' +
         '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      Inntekt inntekt = (Inntekt) o;
      return Double.compare(inntekt.amount, amount) == 0 &&
         Objects.equals(employer, inntekt.employer);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), amount, employer);
   }

}
