package no.nav.foreldrepenger.mottak.oppslag;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.oppslag.Oppslag;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "false", matchIfMissing = true)
public class OppslagTjeneste implements Oppslag {
    private final PDLConnection pdl;
    private final TokenUtil tokenHelper;

    public OppslagTjeneste(PDLConnection pdl, TokenUtil tokenHelper) {
        this.pdl = pdl;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String ping() {
        return pdl.ping();
    }

    @Override
    public Person person() {
        return pdl.hentSøker();
    }

    public Person personMedAlleBarn() {
        return pdl.hentSøkerMedAlleBarn();
    }

    @Override
    public AktørId aktørId() {
        return aktørId(tokenHelper.autentisertBrukerOrElseThrowException());
    }

    @Override
    public AktørId aktørId(Fødselsnummer fnr) {
        return pdl.aktøridFor(fnr);
    }

    /**
     * Ubeskyttet
     */
    @Override
    public Fødselsnummer fnr(AktørId aktørId) {
        return pdl.fødselsnummerFor(aktørId);
    }

    /**
     * Ubeskyttet
     */
    @Override
    public Navn navn(String id) {
        return pdl.navnFor(id);
    }

    /**
     * @return empty hvis person beskyttet/ikke finnes
     */
    public Optional<Navn> annenPartNavn(String id) {
        return pdl.annenPart(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdlConn=" + pdl + ", tokenHelper=" + tokenHelper + "]";
    }
}
