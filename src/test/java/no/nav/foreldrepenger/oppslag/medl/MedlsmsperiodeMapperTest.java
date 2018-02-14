package no.nav.foreldrepenger.oppslag.medl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Medlemsperiode;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.GrunnlagstypeMedTerm;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.LandkodeMedTerm;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.PeriodetypeMedTerm;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.StatuskodeMedTerm;

class MedlsmsperiodeMapperTest {

   @Test
   @Tag("fast")
   public void mapValues() {
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(2);
      Medlemsperiode periode = periode(earlier, now);
      MedlPeriode expected = new MedlPeriode(
         earlier,
         Optional.of(now),
         "statusen",
         "typen",
         "grunnlaget",
         "landet");
      MedlPeriode actual = MedlemsperiodeMapper.map(periode);
      assertEquals(expected, actual);
   }

   private Medlemsperiode periode(LocalDate from, LocalDate to) {
      Medlemsperiode periode = new Medlemsperiode();
      periode.setFraOgMed(CalendarConverter.toXMLGregorianCalendar(from));
      periode.setTilOgMed(CalendarConverter.toXMLGregorianCalendar(to));
      StatuskodeMedTerm status = new StatuskodeMedTerm();
      status.setTerm("statusen");
      periode.setStatus(status);
      PeriodetypeMedTerm type = new PeriodetypeMedTerm();
      type.setTerm("typen");
      periode.setType(type);
      GrunnlagstypeMedTerm grunnlag = new GrunnlagstypeMedTerm();
      grunnlag.setTerm("grunnlaget");
      periode.setGrunnlagstype(grunnlag);
      LandkodeMedTerm land = new LandkodeMedTerm();
      land.setTerm("landet");
      periode.setLand(land);
      return periode;
   }

}
