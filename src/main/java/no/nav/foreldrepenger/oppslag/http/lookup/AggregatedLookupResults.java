package no.nav.foreldrepenger.oppslag.http.lookup;

import no.nav.foreldrepenger.oppslag.http.lookup.aareg.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.http.lookup.inntekt.Inntekt;
import no.nav.foreldrepenger.oppslag.http.lookup.medl.MedlPeriode;
import no.nav.foreldrepenger.oppslag.http.lookup.ytelser.Ytelse;

import java.util.List;

public class AggregatedLookupResults {

    private List<LookupResult<Inntekt>> inntekt;
    private List<LookupResult<Ytelse>> ytelser;
    private List<LookupResult<Arbeidsforhold>> arbeidsforhold;
    private List<LookupResult<MedlPeriode>> medlPerioder;

    public AggregatedLookupResults(
            List<LookupResult<Inntekt>> inntekt,
            List<LookupResult<Ytelse>> ytelser,
            List<LookupResult<Arbeidsforhold>> arbeidsforhold,
            List<LookupResult<MedlPeriode>> medlPerioder) {
        this.inntekt = inntekt;
        this.ytelser = ytelser;
        this.arbeidsforhold = arbeidsforhold;
        this.medlPerioder = medlPerioder;
    }

    public List<LookupResult<Inntekt>> getInntekt() {
        return inntekt;
    }

    public List<LookupResult<Ytelse>> getYtelser() {
        return ytelser;
    }

    public List<LookupResult<Arbeidsforhold>> getArbeidsforhold() {
        return arbeidsforhold;
    }

    public List<LookupResult<MedlPeriode>> getMedlPerioder() {
        return medlPerioder;
    }

    @Override
    public String toString() {
        return "AggregatedLookupResults{" +
                "inntekt=" + inntekt +
                ", ytelser=" + ytelser +
                ", arbeidsforhold=" + arbeidsforhold +
                ", medlPerioder=" + medlPerioder +
                '}';
    }
}
