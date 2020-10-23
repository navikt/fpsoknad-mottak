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
    private final OppslagConnection conn;
    private final PDLConnection pdl;
    private final TokenUtil tokenHelper;

    public OppslagTjeneste(PDLConnection pdl, OppslagConnection conn, TokenUtil tokenHelper) {
        this.conn = conn;
        this.pdl = pdl;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String ping() {
        return conn.ping();
    }

    @Override
    public Person søker() {
        return sammenlign(conn.søker(), pdlPerson());
    }

    private static <T> T sammenlign(T tps, T pdl) {
        String name = tps.getClass().getSimpleName();
        LOG.info("Sammenligner {} ", name);
        if (!tps.equals(pdl)) {
            LOG.warn("TPS-{} og PDL-{} er ulike, tps={}, pdl={}", name, name, tps, pdl);
        } else {
            LOG.info("TPS-{} og PDL-{} er like, {}", name, name, pdl);
        }
        return tps;
    }

    private Person pdlPerson() {
        try {
            var p = pdl.hentSøker();
            var np = new Person(p.getId(), p.getNavn(), p.getFødselsdato(), p.getMålform(), p.getLandKode(), false,
                    p.getBankkonto());
            np.setAktørId(p.getAktørId());
            return np;
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag PDL person");
            return null;
        }
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
        return sammenlign(conn.navn(fnr), pdlNavn());
    }

    private Navn pdlNavn() {
        try {
            return pdl.oppslagNavn(tokenHelper.getSubject());
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag PDL navn");
            return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [conn=" + conn + ", tokenHelper=" + tokenHelper + "]";
    }
}
