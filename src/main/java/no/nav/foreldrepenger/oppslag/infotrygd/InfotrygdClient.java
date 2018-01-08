package no.nav.foreldrepenger.oppslag.infotrygd;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;
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

	public boolean hasCases(String fnr) {
		FinnSakListeRequest req = new FinnSakListeRequest();
		req.setPersonident(fnr);
		try {
			FinnSakListeResponse res = infotrygd.finnSakListe(req);
			return !res.getSakListe().isEmpty();
		} catch (Exception ex) {
			log.warn("Error while reading from Infotrygd", ex);
			throw new RuntimeException("Error while reading from Infotrygd: " + ex.getMessage());
		}
	}

}
