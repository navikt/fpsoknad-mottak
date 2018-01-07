package no.nav.foreldrepenger.aktor;

import java.util.*;

import javax.inject.*;

import org.slf4j.*;
import org.springframework.stereotype.*;

import no.nav.foreldrepenger.domain.*;
import no.nav.foreldrepenger.domain.exceptions.*;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.*;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.*;

@Component
public class AktorIdKlient {
	private static final Logger LOG = LoggerFactory.getLogger(AktorIdKlient.class);

	private final AktoerV2 aktoerV2;

	@Inject
	public AktorIdKlient(AktoerV2 aktoerV2) {
		this.aktoerV2 = Objects.requireNonNull(aktoerV2);
	}

	public AktorId aktorIdForFnr(Fodselsnummer fnr) {
		try {
			HentAktoerIdForIdentResponse res = aktoerV2.hentAktoerIdForIdent(request(fnr));
			return new AktorId(res.getAktoerId());
		} catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
			LOG.warn("Henting av aktørid har feilet", e);
			throw new NotFoundException(e.getMessage());
		}
	}

	private static HentAktoerIdForIdentRequest request(Fodselsnummer fnr) {
		HentAktoerIdForIdentRequest req = new HentAktoerIdForIdentRequest();
		req.setIdent(fnr.getFnr());
		return req;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [aktoerV2=" + aktoerV2 + "]";
	}

}
