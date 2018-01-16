package no.nav.foreldrepenger.oppslag.inntekt;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Income;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Ainntektsfilter;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Formaal;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.PersonIdent;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Uttrekksperiode;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeRequest;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeResponse;

@Component
public class InntektClient {
	private static final Logger log = LoggerFactory.getLogger(InntektClient.class);

	private final InntektV3 inntektV3;

	@Inject
	public InntektClient(InntektV3 inntektV3) {
		this.inntektV3 = inntektV3;
	}

	public List<Income> incomeForPeriod(Fodselsnummer fnr, LocalDate from, LocalDate to) {
		HentInntektListeRequest req = request(fnr, from, to);
		try {
			HentInntektListeResponse res = inntektV3.hentInntektListe(req);
			return res.getArbeidsInntektIdent().getArbeidsInntektMaaned().stream()
			        .flatMap(aim -> aim.getArbeidsInntektInformasjon().getInntektListe().stream())
			        .map(InntektMapper::map).collect(toList());
		} catch (Exception ex) {
			log.warn("Error while retrieving income", ex);
			throw new RuntimeException("Error while retrieving income data: " + ex.getMessage());
		}
	}

	private HentInntektListeRequest request(Fodselsnummer fnr, LocalDate from, LocalDate to) {
		HentInntektListeRequest req = new HentInntektListeRequest();

		PersonIdent person = new PersonIdent();
		person.setPersonIdent(fnr.getFnr());
		req.setIdent(person);

		Ainntektsfilter ainntektsfilter = new Ainntektsfilter();
		ainntektsfilter.setValue("ForeldrepengerA-Inntekt");
		ainntektsfilter.setKodeRef("ForeldrepengerA-Inntekt");
		ainntektsfilter.setKodeverksRef(
		        "http://nav.no/kodeverk/Term/A-inntektsfilter/ForeldrepengerA-Inntekt/nb/Foreldrepenger_20a-inntekt?v=6");
		req.setAinntektsfilter(ainntektsfilter);

		Uttrekksperiode uttrekksperiode = new Uttrekksperiode();
		uttrekksperiode.setMaanedFom(CalendarConverter.toCalendar(from));
		uttrekksperiode.setMaanedTom(CalendarConverter.toCalendar(to));
		req.setUttrekksperiode(uttrekksperiode);

		Formaal formaal = new Formaal();
		formaal.setValue("Foreldrepenger");
		formaal.setKodeRef("Foreldrepenger");
		formaal.setKodeverksRef(
		        "http://nav.no/kodeverk/Term/A-inntektsfilter/ForeldrepengerA-Inntekt/nb/Foreldrepenger_20a-inntekt?v=6");
		req.setFormaal(formaal);

		return req;
	}
}
