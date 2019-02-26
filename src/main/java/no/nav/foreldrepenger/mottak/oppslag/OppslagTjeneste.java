package no.nav.foreldrepenger.mottak.oppslag;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
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
    public Person getSøker() {
        return connection.getSøker();
    }

    @Override
    public AktorId getAktørId() {
        return getAktørId(tokenHelper.autentisertFNR());
    }

    @Override
    @Cacheable(cacheNames = "aktoer")
    public AktorId getAktørId(Fødselsnummer fnr) {
        return connection.getAktørId(fnr);
    }

    @Override
    public Fødselsnummer getFnr(AktorId aktørId) {
        return connection.getFnr(aktørId);
    }

    @Override
    public List<Arbeidsforhold> getArbeidsforhold() {
        return connection.getArbeidsforhold();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", tokenHelper=" + tokenHelper + "]";
    }

}
