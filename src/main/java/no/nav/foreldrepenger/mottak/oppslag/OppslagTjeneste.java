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
    private final PDLConnection pdl;
    private final TokenUtil tokenHelper;
    private static final Logger LOG = LoggerFactory.getLogger(OppslagTjeneste.class);

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
        return pdlPerson();
    }

    @Override
    public AktørId aktørId() {
        return aktørId(tokenHelper.fnr());
    }

    @Override
    public AktørId aktørId(Fødselsnummer fnr) {
        return pdl.aktøridFor(fnr);
    }

    @Override
    public Fødselsnummer fnr(AktørId aktørId) {
        return pdl.fødselsnummerFor(aktørId);
    }

    @Override
    public Navn navn(String id) {
        return pdl.navnFor(id);
    }

    private Person pdlPerson() {
        LOG.info("Authentication level {}", tokenHelper.getLevel());
        var p = pdl.hentSøker();
        var np = new Person(p.getId(), p.getNavn(), p.getFødselsdato(), p.getMålform(), p.getLandKode(),
                p.getBankkonto(), p.getBarn());
        np.setAktørId(p.getAktørId());
        return np;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdlConn=" + pdl + ", tokenHelper=" + tokenHelper + "]";
    }
}
