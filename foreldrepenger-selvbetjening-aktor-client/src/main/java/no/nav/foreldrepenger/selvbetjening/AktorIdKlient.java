package no.nav.foreldrepenger.selvbetjening;

import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.selvbetjening.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;
import no.nav.foreldrepenger.selvbetjening.domain.exceptions.NotFoundException;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;

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
