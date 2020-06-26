package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ArbeidsforholdTjeneste implements Arbeidsforhold {

    private final ArbeidsforholdConnection connection;

    public ArbeidsforholdTjeneste(ArbeidsforholdConnection connection) {
        this.connection = connection;
    }

    @Override
    public List<EnkeltArbeidsforhold> hentAktiveArbeidsforhold() {
        return connection.hentArbeidsforhold();
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection + "]";
    }
}
