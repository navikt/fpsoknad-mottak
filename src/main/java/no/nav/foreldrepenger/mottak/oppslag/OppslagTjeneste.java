package no.nav.foreldrepenger.mottak.oppslag;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(OppslagTjeneste.class);

    public OppslagTjeneste(OppslagConnection connection, TokenUtil tokenHelper) {
        this.connection = connection;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public Person getSøker() {
        return connection.hentSøker();
    }

    @Override
    public AktørId getAktørId() {
        return getAktørId(tokenHelper.autentisertFNR());
    }

    @Override
    // @Cacheable(cacheNames = "aktør")
    public AktørId getAktørId(Fødselsnummer fnr) {
        return connection.hentAktørId(fnr);
    }

    @Override
    public String getAktørIdAsString() {
        return Optional.ofNullable(getAktørId())
                .map(AktørId::getId)
                .orElse(null);
    }

    @Override
    public Fødselsnummer getFnr(AktørId aktørId) {
        return connection.hentFnr(aktørId);
    }

    @Override
    public Navn hentNavn(String fnr) {
        return connection.hentNavn(fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", tokenHelper=" + tokenHelper + "]";
    }

}
