package no.nav.foreldrepenger.oppslag.aareg;

import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.IncompleteRequestException;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Periode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class AaregClient {
	private static final Logger LOG = LoggerFactory.getLogger(AaregClient.class);

	private ArbeidsforholdV3 arbeidsforholdV3;

	@Inject
	public AaregClient(ArbeidsforholdV3 arbeidsforholdV3) {
		this.arbeidsforholdV3 = arbeidsforholdV3;
	}

	public List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr, LocalDate from, LocalDate to) {
      try {
         FinnArbeidsforholdPrArbeidstakerRequest req = request(fnr, from, to);
         FinnArbeidsforholdPrArbeidstakerResponse response = arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(req);
         return response.getArbeidsforhold().stream()
            .map(ArbeidsforholdMapper::map)
            .collect(toList());
      } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning ex) {
         LOG.warn("Sikkehetsfeil fra AAREG", ex);
         throw new ForbiddenException(ex);
      } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput ex) {
         throw new IncompleteRequestException(ex);
      } catch (Exception ex) {
         ex.printStackTrace();
         throw new RuntimeException(ex);
      }
   }

   private FinnArbeidsforholdPrArbeidstakerRequest request(Fodselsnummer fnr, LocalDate from, LocalDate to) {
      FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();

      NorskIdent ident = new NorskIdent();
      ident.setIdent(fnr.getFnr());
      request.setIdent(ident);

      Periode periode = new Periode();
      periode.setFom(CalendarConverter.toXMLGregorianCalendar(from));
      periode.setTom(CalendarConverter.toXMLGregorianCalendar(to));
      request.setArbeidsforholdIPeriode(periode);

      Regelverker regelverker = new Regelverker();
      regelverker.setValue("ALLE");
      request.setRapportertSomRegelverk(regelverker);

      return request;
   }

   @Override
   public String toString() {
      return "AaregClient{" +
         "arbeidsforholdV3=" + arbeidsforholdV3 +
         '}';
   }
}
