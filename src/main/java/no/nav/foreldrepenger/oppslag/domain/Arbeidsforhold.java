package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold extends TidsAvgrensetBrukerInfo {

   private String arbeidsgiverId;
   private String arbeidsgiverIdType;
   private String yrke;

   public Arbeidsforhold (
         String arbeidsgiverId,
         String arbeidsgiverIdType,
         String yrke,
         LocalDate from,
         Optional<LocalDate> to) {
      super(from, to);
      this.arbeidsgiverId = arbeidsgiverId;
      this.arbeidsgiverIdType = arbeidsgiverIdType;
      this.yrke = yrke;
   }

   public String getArbeidsgiverId() {
      return arbeidsgiverId;
   }

   public String getArbeidsgiverIdType() {
      return arbeidsgiverIdType;
   }

   public String getYrke() {
      return yrke;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      Arbeidsforhold that = (Arbeidsforhold) o;
      return Objects.equals(arbeidsgiverId, that.arbeidsgiverId) &&
         Objects.equals(arbeidsgiverIdType, that.arbeidsgiverIdType) &&
         Objects.equals(yrke, that.yrke);
   }

   @Override
   public int hashCode() {

      return Objects.hash(super.hashCode(), arbeidsgiverId, arbeidsgiverIdType, yrke);
   }

   @Override
   public String toString() {
      return "Arbeidsforhold{" +
         "arbeidsgiverId='" + arbeidsgiverId + '\'' +
         ", arbeidsgiverIdType='" + arbeidsgiverIdType + '\'' +
         ", yrke='" + yrke + '\'' +
         '}';
   }
}
