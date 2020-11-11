package no.nav.foreldrepenger.mottak.oppslag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLConnection;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Service
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "false", matchIfMissing = true)
public class OppslagTjeneste implements Oppslag {
    private static final Logger LOG = LoggerFactory.getLogger(OppslagTjeneste.class);
    private final TPSConnection tpsConn;
    private final PDLConnection pdlConn;
    private final TokenUtil tokenHelper;

    public OppslagTjeneste(PDLConnection pdlConn, TPSConnection tpsConn, TokenUtil tokenHelper) {
        this.tpsConn = tpsConn;
        this.pdlConn = pdlConn;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String ping() {
        return pdlConn.ping();
    }

    @Override
    public Person person() {
        return /* sammenlign(tpsConn.søker(), */pdlPerson(); // );
    }

    @Override
    public AktørId aktørId() {
        return aktørId(tokenHelper.fnr());
    }

    @Override
    public AktørId aktørId(Fødselsnummer fnr) {
        return pdlAktørId(fnr);
    }

    @Override
    public Fødselsnummer fnr(AktørId aktørId) {
        return pdlFnr(aktørId);
    }

    @Override
    public Navn navn(String id) {
        return pdlNavn(id);
    }

    private Fødselsnummer pdlFnr(AktørId aktørId) {
        return pdlConn.fødselsnummerFor(aktørId);
    }

    private Person pdlPerson() {

        var p = pdlConn.hentSøker();
        var np = new Person(p.getId(), p.getNavn(), p.getFødselsdato(), p.getMålform(), p.getLandKode(),
                p.getBankkonto());
        np.setAktørId(p.getAktørId());
        return np;
    }

    private AktørId pdlAktørId(Fødselsnummer fnr) {
        return pdlConn.aktøridFor(fnr);
    }

    private Navn pdlNavn(String id) {
        return pdlConn.navnFor(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tpsConn=" + tpsConn + ", pdlConn=" + pdlConn + ", tokenHelper=" + tokenHelper + "]";
    }
}
