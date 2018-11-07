package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.UnauthorizedException;
import no.nav.foreldrepenger.errorhandling.IncompleteRequestException;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ArbeidsforholdClientWs implements ArbeidsforholdClient {
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientWs.class);

    private final ArbeidsforholdV3 arbeidsforholdV3;
    private final ArbeidsforholdV3 healthIndicator;
    private final OrganisasjonClient orgClient;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.aareg");

    public ArbeidsforholdClientWs(ArbeidsforholdV3 arbeidsforholdV3, ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient orgClient) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
        this.healthIndicator = healthIndicator;
        this.orgClient = orgClient;
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger AAreg");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    @Override
    @Timed("lookup.arbeidsforhold")
    public List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr) {
        try {
            FinnArbeidsforholdPrArbeidstakerResponse response = arbeidsforholdV3
                    .finnArbeidsforholdPrArbeidstaker(request(fnr));

            return response.getArbeidsforhold().stream()
                    .map(ArbeidsforholdMapper::map)
                    .filter(this::isOngoing)
                    .map(this::addArbeidsgiverNavn)
                    .collect(toList());
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning ex) {
            LOG.warn("Sikkerhetsfeil fra AAREG", ex);
            throw new UnauthorizedException(ex);
        } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput ex) {
            throw new IncompleteRequestException(ex);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }
    }

    private FinnArbeidsforholdPrArbeidstakerRequest request(Fødselsnummer fnr) {
        FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();

        NorskIdent ident = new NorskIdent();
        ident.setIdent(fnr.getFnr());
        request.setIdent(ident);

        Regelverker regelverker = new Regelverker();
        regelverker.setValue("ALLE");
        request.setRapportertSomRegelverk(regelverker);

        return request;
    }

    private Arbeidsforhold addArbeidsgiverNavn(Arbeidsforhold arbeidsforhold) {
        arbeidsforhold.setArbeidsgiverNavn(orgClient.nameFor(arbeidsforhold.getArbeidsgiverId()).orElse("Ukjent navn"));
        return arbeidsforhold;
    }

    @Override
    public String toString() {
        return "AaregClient{" +
                "arbeidsforholdV3=" + arbeidsforholdV3 +
                '}';
    }

    protected boolean isOngoing(Arbeidsforhold arbeidsforhold) {
        LocalDate today = LocalDate.now();
        return arbeidsforhold.getTom()
            .map(t -> t.isAfter(today) || t.equals(today))
            .orElse(true);
    }
}
