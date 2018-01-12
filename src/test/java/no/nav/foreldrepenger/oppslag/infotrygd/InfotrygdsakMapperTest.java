package no.nav.foreldrepenger.oppslag.infotrygd;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import javax.xml.datatype.DatatypeFactory;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.Status;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.Tema;

public class InfotrygdsakMapperTest {

   @Test
   public void mapValues() throws Exception {
      InfotrygdSak sak = new InfotrygdSak();
      sak.setVedtatt(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13"));
      Status status = new Status();
      status.setTermnavn("statusen");
      sak.setStatus(status);
      Tema tema = new Tema();
      tema.setTermnavn("temaet");
      sak.setTema(tema);
      LocalDate date = LocalDate.of(2017, 12, 13);
      Ytelse expected = new Ytelse(
         "temaet",
         "statusen",
         date, Optional.empty(),
         "Infotrygd");
      Ytelse actual = InfotrygdsakMapper.map(sak);
      assertEquals(expected, actual);
   }

}
