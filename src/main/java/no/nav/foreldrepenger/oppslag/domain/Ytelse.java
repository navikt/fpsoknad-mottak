package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Ytelse {

   private String type;
   private String status;
   private LocalDate from;
   private Optional<LocalDate> to;

   public Ytelse(String type, String status, LocalDate from, Optional<LocalDate> to) {
      this.type = type;
      this.status = status;
      this.from = from;
      this.to = to;
   }

   public String getType() {
      return type;
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
      return "Ytelse{" +
         "type='" + type + '\'' +
         ", status='" + status + '\'' +
         ", from=" + from +
         ", to=" + to +
         '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Ytelse ytelse = (Ytelse) o;
      return Objects.equals(type, ytelse.type) &&
         Objects.equals(status, ytelse.status) &&
         Objects.equals(from, ytelse.from) &&
         Objects.equals(to, ytelse.to);
   }

   @Override
   public int hashCode() {

      return Objects.hash(type, status, from, to);
   }
}
