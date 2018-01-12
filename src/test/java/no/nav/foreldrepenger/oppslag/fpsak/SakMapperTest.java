package no.nav.foreldrepenger.oppslag.fpsak;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Optional;

import javax.xml.datatype.DatatypeFactory;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Behandlingstema;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Saksstatus;

public class SakMapperTest {

   @Test
   public void mapValues() throws Exception {
      Sak sak = new Sak();
      Behandlingstema tema = new Behandlingstema();
      tema.setTermnavn("termen");
      sak.setBehandlingstema(tema);
      Saksstatus status = new Saksstatus();
      status.setTermnavn("statusen");
      sak.setStatus(status);
      sak.setOpprettet(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13"));
      Ytelse expected = new Ytelse(
         "termen",
         "statusen",
         LocalDate.of(2017, 12, 13),
         Optional.empty(),
         "FPSAK");
      Ytelse actual = SakMapper.map(sak);
      assertEquals(expected, actual);
   }

}
