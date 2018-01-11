package no.nav.foreldrepenger.oppslag.arena;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.HentYtelseskontraktListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.YtelseskontraktV3;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Periode;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeRequest;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeResponse;

@Component
public class ArenaClient {
   private static final Logger log = LoggerFactory.getLogger(ArenaClient.class);

   private YtelseskontraktV3 ytelseskontraktV3;

   @Inject
   public ArenaClient(YtelseskontraktV3 ytelseskontraktV3) {
      this.ytelseskontraktV3 = ytelseskontraktV3;
   }

   public List<Ytelse> ytelser(String fnr, LocalDate from, LocalDate to) {
      HentYtelseskontraktListeRequest req = new HentYtelseskontraktListeRequest();
      Periode periode = new Periode();
      periode.setFom(CalendarConverter.toCalendar(from));
      periode.setTom(CalendarConverter.toCalendar(to));
      req.setPeriode(periode);
      req.setPersonidentifikator(fnr);
      try {
         HentYtelseskontraktListeResponse res = ytelseskontraktV3.hentYtelseskontraktListe(req);
         return res.getYtelseskontraktListe().stream()
            .map(YtelseskontraktMapper::map)
            .collect(toList());
      } catch(HentYtelseskontraktListeSikkerhetsbegrensning ex) {
         log.warn("Security error from Arena", ex);
         throw new ForbiddenException(ex);
      } catch (Exception ex) {
         log.warn("Error while retrieving ytelse", ex);
         throw new RuntimeException("Error while retrieving ytelse: " + ex.getMessage());
      }
   }
}
