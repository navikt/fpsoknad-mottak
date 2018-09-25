package no.nav.foreldrepenger.lookup.ws.inntekt;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.errorhandling.IncompleteRequestException;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.time.DateUtil;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.HentInntektListeHarIkkeTilgangTilOensketAInntektsfilter;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.HentInntektListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.HentInntektListeUgyldigInput;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Ainntektsfilter;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Formaal;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.PersonIdent;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Uttrekksperiode;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeRequest;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeResponse;

public class InntektClientWs implements InntektClient {
    private static final Logger LOG = LoggerFactory.getLogger(InntektClientWs.class);

    private final InntektV3 inntektV3;
    private final InntektV3 healthIndicator;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.inntekt");

    public InntektClientWs(InntektV3 inntektV3, InntektV3 healthIndicator) {
        this.inntektV3 = inntektV3;
        this.healthIndicator = healthIndicator;
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger Inntekt");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    @Override
    public List<Inntekt> incomeForPeriod(Fødselsnummer fnr, LocalDate from, LocalDate to) {
        HentInntektListeRequest req = request(fnr, from, to);
        try {
            HentInntektListeResponse res = inntektV3.hentInntektListe(req);
            return res.getArbeidsInntektIdent().getArbeidsInntektMaaned().stream()
                    .flatMap(aim -> aim.getArbeidsInntektInformasjon().getInntektListe().stream())
                    .map(InntektMapper::map).collect(toList());
        } catch (HentInntektListeHarIkkeTilgangTilOensketAInntektsfilter | HentInntektListeSikkerhetsbegrensning e) {
            LOG.warn("Error while retrieving income", e);
            throw new ForbiddenException(e);
        } catch (HentInntektListeUgyldigInput e) {
            LOG.warn("Error while retrieving income", e);
            throw new IncompleteRequestException(e);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }
    }

    private HentInntektListeRequest request(Fødselsnummer fnr, LocalDate from, LocalDate to) {
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
        uttrekksperiode.setMaanedFom(DateUtil.toXMLGregorianCalendar(from));
        uttrekksperiode.setMaanedTom(DateUtil.toXMLGregorianCalendar(to));
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
