package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.oppslag.organisasjon.OrganisasjonConnection;

@Service
public class ArbeidsforholdTjenste implements Arbeidsforhold {

    private final ArbeidsforholdConnection connection;
    private final OrganisasjonConnection orgConnection;

    public ArbeidsforholdTjenste(ArbeidsforholdConnection connection, OrganisasjonConnection orgConnection) {
        this.connection = connection;
        this.orgConnection = orgConnection;
    }

    @Override
    public List<EnkeltArbeidsforhold> hentAktiveArbeidsforhold() {
        var arbeidsforhold = connection.hentArbeidsforhold();
        arbeidsforhold.stream()
                .forEach(a -> a.setArbeidsgiverNavn(orgConnection.organisasjonsNavn(a.getArbeidsgiverId())));
        return arbeidsforhold;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection + ", orgConnection=" + orgConnection + "]";
    }

}
