package no.nav.foreldrepenger.oppslag.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Ytelse extends TidsAvgrensetBrukerInfo {
   private String type;
   private String status;

   public Ytelse(String type, String status, LocalDate from) {
      this(type, status, from, Optional.empty());
   }

   @JsonCreator
   public Ytelse(
      @JsonProperty("type") String type,
      @JsonProperty("status ")String status,
      @JsonProperty("from") LocalDate from,
      @JsonProperty("to" )Optional<LocalDate> to) {
      super(from, to);
      this.type = type;
      this.status = status;
   }

   public String getType() {
      return type;
   }

   public String getStatus() {
      return status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      Ytelse ytelse = (Ytelse) o;
      return Objects.equals(type, ytelse.type) &&
         Objects.equals(status, ytelse.status);
   }

   @Override
   public int hashCode() {

      return Objects.hash(super.hashCode(), type, status);
   }

   @Override
   public String toString() {
      return super.toString() + "Ytelse{" +
         "type='" + type + '\'' +
         ", status='" + status + '\'' +
         '}';
   }
}
