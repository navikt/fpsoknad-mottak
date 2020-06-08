package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ArbeidsforholdTjenste {

    private final ArbeidsforholdConnection connection;

    public ArbeidsforholdTjenste(ArbeidsforholdConnection connection) {
        this.connection = connection;
    }

    public List<Arbeidsforhold> hentAktiveArbeidsforhold() {
        return connection.hentArbeidsforhold();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection + "]";
    }
}
