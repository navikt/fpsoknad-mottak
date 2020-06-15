package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ArbeidsforholdTjenste implements Arbeidsforhold {

    private final ArbeidsforholdConnection connection;

    public ArbeidsforholdTjenste(ArbeidsforholdConnection connection) {
        this.connection = connection;
    }

    @Override
    public List<EnkeltArbeidsforhold> hentAktiveArbeidsforhold() {
        return connection.hentArbeidsforhold();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection + "]";
    }
}
