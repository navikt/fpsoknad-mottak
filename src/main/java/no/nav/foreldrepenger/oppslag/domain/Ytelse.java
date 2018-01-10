package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;

public class Ytelse {

   private String type;
   private String status;
   private LocalDate from;
   private LocalDate to;

   public Ytelse(String type, String status, LocalDate from, LocalDate to) {
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

   public LocalDate getTo() {
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
}
