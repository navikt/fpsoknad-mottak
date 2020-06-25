package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Service
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "false", matchIfMissing = true)
public class OppslagTjeneste implements Oppslag {
    private final OppslagConnection connection;
    private final TokenUtil tokenHelper;

    public OppslagTjeneste(OppslagConnection connection, TokenUtil tokenHelper) {
        this.connection = connection;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public Person hentSøker() {
        return connection.hentSøker();
    }

    @Override
    public AktørId hentAktørId() {
        return hentAktørId(tokenHelper.autentisertFNR());
    }

    @Override
    public AktørId hentAktørId(Fødselsnummer fnr) {
        return connection.hentAktørId(fnr);
    }

    @Override
    public Fødselsnummer hentFnr(AktørId aktørId) {
        return connection.hentFnr(aktørId);
    }

    @Override
    public Navn hentNavn(Fødselsnummer fnr) {
        return connection.hentNavn(fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", tokenHelper=" + tokenHelper + "]";
    }
}
