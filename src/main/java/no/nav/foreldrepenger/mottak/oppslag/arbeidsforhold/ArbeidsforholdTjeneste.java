package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold;

import java.util.List;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.http.RetryAware;

@Service
public class ArbeidsforholdTjeneste implements RetryAware, ArbeidsInfo {
    private final ArbeidsforholdConnection connection;
    private final OrganisasjonConnection orgConnection;

    public ArbeidsforholdTjeneste(ArbeidsforholdConnection connection, OrganisasjonConnection orgConnection) {
        this.connection = connection;
        this.orgConnection = orgConnection;
    }

    @Override
    public List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        return connection.hentArbeidsforhold();
    }

    @Override
    public String orgnavn(String orgnr) {
        return orgConnection.navn(orgnr);
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", orgConnection=" + orgConnection + "]";
    }
}
