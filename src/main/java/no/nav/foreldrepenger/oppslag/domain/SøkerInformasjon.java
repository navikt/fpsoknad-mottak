package no.nav.foreldrepenger.oppslag.domain;

import java.util.List;
import java.util.Objects;

public class SøkerInformasjon {

    private final Person person;
    private final List<LookupResult<Inntekt>> inntekter;
    private final List<LookupResult<Ytelse>> ytelser;

    public SøkerInformasjon(Person person, List<LookupResult<Inntekt>> inntekter, List<LookupResult<Ytelse>> ytelser) {
        this.person = Objects.requireNonNull(person);
        this.inntekter = Objects.requireNonNull(inntekter);
        this.ytelser = Objects.requireNonNull(ytelser);

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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + ", inntekter=" + inntekter + ", ytelser=" + ytelser
                + "]";
    }

}
