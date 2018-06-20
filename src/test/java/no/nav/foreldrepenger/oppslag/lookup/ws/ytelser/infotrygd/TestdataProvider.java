package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.infotrygd;

import no.nav.foreldrepenger.oppslag.time.DateUtil;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.InfotrygdSak;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.Status;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.Tema;

import java.time.LocalDate;

public class TestdataProvider {

   public static InfotrygdSak sak() {
      InfotrygdSak sak = new InfotrygdSak();
      sak.setVedtatt(DateUtil.toXMLGregorianCalendar(LocalDate.of(2017, 12, 13)));
      Status status = new Status();
      status.setTermnavn("statusen");
      sak.setStatus(status);
      Tema tema = new Tema();
      tema.setTermnavn("typen");
      sak.setTema(tema);
      return sak;
   }

}
