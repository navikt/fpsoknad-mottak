package no.nav.foreldrepenger.mottak.oversikt;

import java.util.List;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

@Service
public class OversiktTjeneste implements Oversikt {
    private final OversiktConnection connection;

    public OversiktTjeneste(OversiktConnection connection) {
        this.connection = connection;
    }

    @Override
    public List<EnkeltArbeidsforhold> hentArbeidsforhold() {
        return connection.hentArbeidsforhold();
    }

    @Override
    public PersonDto personinfo(Ytelse ytelse) {
        return connection.hentPersoninfo(ytelse);
    }

    @Override
    public AktørId konverter(Fødselsnummer fnr) {
        return connection.aktørId(fnr);
    }
}
