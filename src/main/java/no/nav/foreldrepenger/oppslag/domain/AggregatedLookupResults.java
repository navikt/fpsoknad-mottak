package no.nav.foreldrepenger.oppslag.domain;

import java.util.List;

public class AggregatedLookupResults {

   private List<LookupResult<Inntekt>> inntekt;
   private List<LookupResult<Ytelse>> ytelser;
   private List<LookupResult<Arbeidsforhold>> arbeidsforhold;

   public AggregatedLookupResults(
         List<LookupResult<Inntekt>> inntekt,
         List<LookupResult<Ytelse>> ytelser,
         List<LookupResult<Arbeidsforhold>> arbeidsforhold) {
      this.inntekt = inntekt;
      this.ytelser = ytelser;
      this.arbeidsforhold = arbeidsforhold;
   }

   public List<LookupResult<Inntekt>> getInntekt() {
      return inntekt;
   }

   public List<LookupResult<Ytelse>> getYtelser() {
      return ytelser;
   }

   public List<LookupResult<Arbeidsforhold>> getArbeidsforhold() {
      return arbeidsforhold;
   }

   @Override
   public String toString() {
      return "AggregatedLookupResults{" +
         "inntekt=" + inntekt +
         ", ytelser=" + ytelser +
         ", arbeidsforhold=" + arbeidsforhold +
         '}';
   }
}
