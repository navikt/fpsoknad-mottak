package no.nav.foreldrepenger.oppslag.aareg;

import static org.junit.jupiter.api.Assertions.*;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.AnsettelsesPeriode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforholdstyper;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Gyldighetsperiode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

public class ArbeidsforholdMapperTest {

   @Test
   public void mapValues() {
      no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold =
         new no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold();
      Organisasjon org = new Organisasjon();
      org.setAktoerId("12345");
      forhold.setArbeidsgiver(org);
      Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();
      LocalDate now = LocalDate.now();
      LocalDate earlier = now.minusMonths(2);
      gyldighetsperiode.setFom(CalendarConverter.toXMLGregorianCalendar(earlier));
      gyldighetsperiode.setTom(CalendarConverter.toXMLGregorianCalendar(now));
      AnsettelsesPeriode ansettelsesperiode = new AnsettelsesPeriode();
      ansettelsesperiode.setPeriode(gyldighetsperiode);
      forhold.setAnsettelsesPeriode(ansettelsesperiode);
      Arbeidsforholdstyper type = new Arbeidsforholdstyper();
      type.setValue("typen");
      forhold.setArbeidsforholdstype(type);
      Arbeidsforhold expected =
         new Arbeidsforhold(new AktorId("12345"), "typen", earlier, Optional.of(now));
      Arbeidsforhold actual = ArbeidsforholdMapper.map(forhold);
      assertEquals(expected, actual);
   }

}
