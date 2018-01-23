package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold extends TidsAvgrensetBrukerInfo {

   private AktorId arbeidsgiver;

   public AktorId getArbeidsgiver() {
      return arbeidsgiver;
   }

   public Arbeidsforhold(AktorId arbeidsgiver, String status, LocalDate from, Optional<LocalDate> to) {
      super(from, to);
      this.arbeidsgiver = arbeidsgiver;

   }

   @Override
   public String toString() {
      return super.toString() + ", " + "arbeidsgiver=" + arbeidsgiver;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      Arbeidsforhold that = (Arbeidsforhold) o;
      return Objects.equals(arbeidsgiver, that.arbeidsgiver);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), arbeidsgiver);
   }
}
