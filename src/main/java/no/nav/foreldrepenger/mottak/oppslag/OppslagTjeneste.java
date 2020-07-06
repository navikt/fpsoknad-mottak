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
    private final OppslagConnection conn;
    private final TokenUtil tokenHelper;

    public OppslagTjeneste(OppslagConnection conn, TokenUtil tokenHelper) {
        this.conn = conn;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String ping() {
        return conn.ping();
    }

    @Override
    public Person søker() {
        return conn.søker();
    }

    @Override
    public AktørId aktørId() {
        return aktørId(tokenHelper.fnr());
    }

    @Override
    public AktørId aktørId(Fødselsnummer fnr) {
        return conn.aktørId(fnr);
    }

    @Override
    public Fødselsnummer fnr(AktørId aktørId) {
        return conn.fnr(aktørId);
    }

    @Override
    public Navn navn(Fødselsnummer fnr) {
        return conn.navn(fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [conn=" + conn + ", tokenHelper=" + tokenHelper + "]";
    }
}
