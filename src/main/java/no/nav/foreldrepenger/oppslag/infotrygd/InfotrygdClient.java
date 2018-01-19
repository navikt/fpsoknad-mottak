package no.nav.foreldrepenger.oppslag.infotrygd;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.FinnSakListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.FinnSakListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.meldinger.FinnSakListeRequest;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.meldinger.FinnSakListeResponse;

@Component
public class InfotrygdClient {
	private static final Logger log = LoggerFactory.getLogger(InfotrygdClient.class);

	private final InfotrygdSakV1 infotrygd;

	@Inject
	public InfotrygdClient(InfotrygdSakV1 infotrygd) {
		this.infotrygd = infotrygd;
	}

	public List<Ytelse> casesFor(Fodselsnummer fnr, LocalDate from, LocalDate to) {
		FinnSakListeRequest req = new FinnSakListeRequest();
		Periode periode = new Periode();
		periode.setFom(CalendarConverter.toXMLGregorianCalendar(from));
		periode.setTom(CalendarConverter.toXMLGregorianCalendar(to));
		req.setPeriode(periode);
		req.setPersonident(fnr.getFnr());
		try {
			FinnSakListeResponse res = infotrygd.finnSakListe(req);
			return res.getSakListe().stream().map(InfotrygdsakMapper::map).collect(toList());
		} catch (FinnSakListePersonIkkeFunnet ex) {
			throw new NotFoundException(ex);
		} catch (FinnSakListeSikkerhetsbegrensning ex) {
			log.warn("Security error from Infotrygd", ex);
			throw new ForbiddenException(ex);
		} catch (Exception ex) {
			log.warn("Error while reading from Infotrygd", ex);
			throw new RuntimeException("Error while reading from Infotrygd", ex);
		}
	}

}
