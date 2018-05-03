package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import no.nav.foreldrepenger.oppslag.lookup.ws.aareg.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.lookup.ws.inntekt.Inntekt;
import no.nav.foreldrepenger.oppslag.lookup.LookupResult;
import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.foreldrepenger.oppslag.lookup.ws.medl.MedlPeriode;

import java.util.List;
import java.util.Objects;

public class SøkerInformasjon {

    private final Person person;
    private final List<LookupResult<Inntekt>> inntekter;
    private final List<LookupResult<Ytelse>> ytelser;
    private final List<LookupResult<Arbeidsforhold>> arbeidsforhold;
    private final List<LookupResult<MedlPeriode>> medlPerioder;

    public SøkerInformasjon(
            Person person,
            List<LookupResult<Inntekt>> inntekter,
            List<LookupResult<Ytelse>> ytelser,
            List<LookupResult<Arbeidsforhold>> arbeidsforhold,
            List<LookupResult<MedlPeriode>> medlPerioder) {
        this.person = Objects.requireNonNull(person);
        this.inntekter = Objects.requireNonNull(inntekter);
        this.ytelser = Objects.requireNonNull(ytelser);
        this.arbeidsforhold = Objects.requireNonNull(arbeidsforhold);
        this.medlPerioder = Objects.requireNonNull(medlPerioder);
    }

    public List<LookupResult<Ytelse>> getYtelser() {
        return ytelser;
    }

    public Person getPerson() {
        return person;
    }

    public List<LookupResult<Inntekt>> getInntekter() {
        return inntekter;
    }

    public List<LookupResult<Arbeidsforhold>> getArbeidsforhold() {
        return arbeidsforhold;
    }

    public List<LookupResult<MedlPeriode>> getMedlPerioder() {
        return medlPerioder;
    }

    @Override
    public String toString() {
        return "SøkerInformasjon{" +
                "person=" + person +
                ", inntekter=" + inntekter +
                ", ytelser=" + ytelser +
                ", arbeidsforhold=" + arbeidsforhold +
                ", medlPerioder=" + medlPerioder +
                '}';
    }
}
